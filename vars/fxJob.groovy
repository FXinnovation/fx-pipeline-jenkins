import com.fxinnovation.data.ScmInfo
import com.fxinnovation.di.IOC
import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.event_data.PipelineEventData
import com.fxinnovation.helper.ClosureHelper
import com.fxinnovation.observer.EventDispatcher

def call(Map closures = [:], List propertiesConfig = [], Map config = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'checkoutCredentialID', CharSequence, '')
  mapAttributeCheck(config, 'checkoutDirectory', CharSequence, '')
  mapAttributeCheck(config, 'checkoutRepositoryURL', CharSequence, '')
  mapAttributeCheck(config, 'checkoutTag', CharSequence, '')
  mapAttributeCheck(config, 'dockerRegistry', CharSequence, '')
  mapAttributeCheck(config, 'dockerRegistryCredentialId', CharSequence, 'jenkins-fxinnovation-dockerhub')
  mapAttributeCheck(config, 'dockerRegistryLogin', Boolean, true)
  mapAttributeCheck(config, 'launchLocally', Boolean, false)
  mapAttributeCheck(config, 'podCloud', CharSequence, 'kubernetes')
  mapAttributeCheck(config, 'podImageName', CharSequence, 'fxinnovation/jenkinsk8sslave')
  mapAttributeCheck(config, 'podImageVersion', CharSequence, 'latest')
  mapAttributeCheck(config, 'podName', CharSequence, 'jenkins-slave-linux')
  mapAttributeCheck(config, 'podNamespace', CharSequence, 'default')
  mapAttributeCheck(config, 'podNodeUsageMode', CharSequence, 'NORMAL')
  mapAttributeCheck(config, 'podVolumes', List, [])
  mapAttributeCheck(config, 'preCommitDockerImageName', CharSequence, 'fxinnovation/pre-commit:latest')
  mapAttributeCheck(config, 'runKind', Boolean, false)
  mapAttributeCheck(config, 'slaveSize', CharSequence, 'small')
  mapAttributeCheck(config, 'timeoutTime', Integer, 10)
  mapAttributeCheck(config, 'timeoutUnit', CharSequence, 'HOURS')
  mapAttributeCheck(config, 'headerMessage', CharSequence, """
\u001b[35m
/!\\ PULL REQUEST /!\\


  _,-""`""-~`)
(`~_,=========\\
 |---,___.-.__,\\
 |        o     \\ ___  _,,,,_     _.--.
  \\      `^`    /`_.-"~      `~-;`     \\
   \\_      _  .'                 `,     |
     |`-                           \\'__/
    /                      ,_       \\  `'-.
   /    .-""~~--.            `"-,   ;_    /
  |              \\               \\  | `""`
   \\__.--'`"-.   /_               |'
              `"`  `~~~---..,     |
                             \\ _.-'`-.
                              \\       \\
                               '.     /
                                 `"~"`
This is Tedi.
Tedi is a bear, and, Tedi is lazy.
His favorite activity is watching the soothing passage of log outputs.
Because he's a bear, Tedi doesn't need to review \033[0;4m\033[0;1m\u001b[35mPULL REQUESTS\u001b[0m\u001b[35m.
Please, do not be like Tedi, don't stay here to watch logs scrolling. He will take care of that for you.
You can click on the following link to review you \033[0;4m\033[0;1m\u001b[35mPULL REQUESTS\u001b[0m\u001b[35m assigned while tedi watch logs for you.

https://scm.dazzlingwrench.fxinnovation.com/pulls?type=assigned&repo=0&sort=&state=open
\u001B[0m
  """)

  if (env.JENKINS_URL == "https://ci.ops0.fxinnovation.com/") {
    config.podVolumes.add(persistentVolumeClaim(claimName: 'jenkins-slave-cache', mountPath: '/cache', readOnly: false))
  }

  closureHelper = new ClosureHelper(this, closures)
  closureHelper.throwErrorIfNotDefined('pipeline')

  defaultPropertiesConfig = [
    buildDiscarder(
      logRotator(
        artifactDaysToKeepStr: '',
        artifactNumToKeepStr: '10',
        daysToKeepStr: '',
        numToKeepStr: '10'
      )
    ),
    pipelineTriggers(
      [cron('@weekly')]
    )
  ]

  try {
    def systemEnv = System.getenv()

    if (systemEnv['JENKINS_LOCAL'] != null) {
      printDebug('config.launchLocally set to “true” because the JENKINS_LOCAL is not null.')
      config.launchLocally = true
    }

    if (systemEnv['JENKINS_DOCKER_DATA_BASEPATH'] != null) {
      printDebug('config.dockerDataBasepath set to “true” because the JENKINS_DOCKER_DATA_BASEPATH is not null.')
      config.dockerDataBasepath = systemEnv['JENKINS_DOCKER_DATA_BASEPATH']
    }
  } catch (RejectedAccessException) {
    printDebug('Cannot access to system environment variables.')
  }

  def slaveSizes = [
    small : [
      resourceRequestCpu   : '100m',
      resourceLimitCpu     : '500m',
      resourceRequestMemory: '1024Mi',
      resourceLimitMemory  : '2048Mi',
    ],
    medium: [
      resourceRequestCpu   : '500m',
      resourceLimitCpu     : '1',
      resourceRequestMemory: '1512Mi',
      resourceLimitMemory  : '2048Mi',
    ],
    large : [
      resourceRequestCpu   : '1500m',
      resourceLimitCpu     : '2',
      resourceRequestMemory: '2048Mi',
      resourceLimitMemory  : '3072Mi',
    ]
  ]

  def chosenSlaveSize = slaveSizes[config.slaveSize]

  def jnlpContainerTemplate = containerTemplate(
    name: 'jnlp',
    image: "${config.podImageName}:${config.podImageVersion}",
    args: '${computer.jnlpmac} ${computer.name}',
    privileged: true,
    alwaysPullImage: true,
    workingDir: '/home/jenkins',
    resourceRequestCpu: chosenSlaveSize.resourceRequestCpu,
    resourceLimitCpu: chosenSlaveSize.resourceLimitCpu,
    resourceRequestMemory: chosenSlaveSize.resourceRequestMemory,
    resourceLimitMemory: chosenSlaveSize.resourceLimitMemory,
  )

  def kindContainerTemplate = containerTemplate(
    name: 'kind',
    image: "fxinnovation/kind:0.2.0",
    privileged: true,
    alwaysPullImage: true,
    workingDir: '/data',
    envVars: [
      envVar(key: 'DOCKERD_PORT', value: '2376'),
      envVar(key: 'KIND_LOGLEVEL', value: 'debug'),
    ],
    resourceRequestCpu: slaveSizes.large.resourceRequestCpu,
    resourceLimitCpu: slaveSizes.large.resourceLimitCpu,
    resourceRequestMemory: slaveSizes.large.resourceRequestMemory,
    resourceLimitMemory: slaveSizes.large.resourceLimitMemory,
  )

  def podContainers = [jnlpContainerTemplate]
  def podInit = ""

  if (config.runKind && !config.launchLocally) {
    podContainers << kindContainerTemplate
    podInit = """
apiVersion: v1
kind: Pod
spec:
  initContainers:
  - name: init-inotify
    image: alpine:latest
    command: ["sysctl", "-w", "fs.inotify.max_user_watches=524288"]
    hostNetwork: true
    hostPID: true
    hostIPC: true
    securityContext:
      privileged: true
    volumeMounts:
      - mountPath: "/sys"
        name: "sys"
  volumes:
  - hostPath:
      path: "/sys"
    name: "sys"
"""
  }

  printDebug("original properties:" + propertiesConfig)
  printDebug("default properties:" + defaultPropertiesConfig)

  // Add default configuration to current configuration without override
  defaultPropertiesConfig.eachWithIndex {element, index ->
    if ((element instanceof Set) && (element.containsKey['$class'])) {
      if (!propertiesConfig.any {
        it.containsKey('$class') ? it['$class'] == element['$class'] : false
      }) {
        propertiesConfig += element.clone()
      }
    } else {
      if (!propertiesConfig.any {
        it.getSymbol() == element.getSymbol()
      }) {
        propertiesConfig += element
      }
    }
  }

  printDebug("computed properties:" + propertiesConfig)
  properties(propertiesConfig)

  def label = UUID.randomUUID().toString()

  printDebug("Launch without kube: " + config.launchLocally)

  if (!config.launchLocally) {
    podTemplate(
      cloud: config.podCloud,
      name: config.podName,
      namespace: config.podNamespace,
      nodeUsageMode: config.podNodeUsageMode,
      idleMinutes: 0,
      slaveConnectTimeout: 100,
      podRetention: never(),
      label: label,
      containers: podContainers,
      yaml: podInit,
      yamlMergeStrategy: merge(),
      volumes: config.podVolumes,
      annotations: [
        podAnnotation(
          key: 'cluster-autoscaler.kubernetes.io/safe-to-evict',
          value: 'false'
        )
      ]
    ) {
      node(label) {
        container('jnlp') {
          pipeline(config, closures)
        }
      }
    }
  } else {
    node() {
      pipeline(config, closures)
    }
  }
}

private def pipeline(Map config, Map closures) {
  EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())
  def status = 'SUCCESS'

  PipelineEventData pipelineEventData = new PipelineEventData(
    config.checkoutDirectory,
    config.checkoutCredentialID,
    config.checkoutRepositoryURL,
    config.checkoutTag,
    config.dockerRegistryLogin,
    config.dockerRegistry,
    config.dockerRegistryCredentialId,
  )


  if ("" != config.headerMessage) {
    ansiColor('xterm') {
      println config.headerMessage
    }
  }


  timeout(
    time: config.timeoutTime,
    unit: config.timeoutUnit
  ) {
    try {
      ansiColor('xterm') {
        stage('prepare') {
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PRE_PREPARE, pipelineEventData)
          closureHelper.execute('prePrepare')

          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PREPARE, pipelineEventData)

          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.POST_PREPARE, pipelineEventData)
          if (closureHelper.isDefined('postPrepare')) {
            closures.postPrepare(IOC.get(ScmInfo.class.getName()))
          }

          if (fileExists('.pre-commit-config.yaml') || fileExists('.pre-commit-config.yml')) {
            preCommitCommand = dockerRunCommand(
              dockerImage: config.preCommitDockerImageName,
              fallbackCommand: 'pre-commit',
              command: 'run -a --color=always',
              dataIsCurrentDirectory: config.dockerDataIsCurrentDirectory,
              dataBasepath: config.dockerDataBasepath,
            )

            stage('preCommit') {
              execute(script: "${preCommitCommand}")
            }
          }
        }

        closureHelper.executeWithinStage('prePipeline')

        stage('pipeline') {
          closures.pipeline(IOC.get(ScmInfo.class.getName()))
        }

        closureHelper.executeWithinStage('postPipeline')
      }
    } catch (error) {
      status = 'FAILURE'
      throw error
    } finally {
      stage('notification') {
        closureHelper.execute('preNotification')

        // We use notification because notify is a reserved keyword in groovy.
        if (closureHelper.isDefined('notification')) {
          closures.notification(status)
        } else {
          fx_notify(
            status: status,
            failOnError: false
          )
        }

        closureHelper.execute('postNotification')
      }
      stage('cleanup') {
        closureHelper.execute('preCleanup')
        if (!config.launchLocally) {
          cleanWs()
        }
        closureHelper.execute('postCleanup')
      }

      if (config.runKind && !config.launchLocally) {
        stage('kindlogs') {
          containerLog(
            name: 'kind'
          )
        }
      }
    }
  }
}