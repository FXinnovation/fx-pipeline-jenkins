import com.fxinnovation.data.ScmInfo
import com.fxinnovation.deprecation.DeprecatedFunction
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
  mapAttributeCheck(config, 'defaultBranchName', CharSequence, 'master')
  mapAttributeCheck(config, 'dockerRegistry', CharSequence, '')
  mapAttributeCheck(config, 'dockerRegistryCredentialId', CharSequence, 'jenkins-fxinnovation-dockerhub')
  mapAttributeCheck(config, 'dockerRegistryLogin', Boolean, true)
  mapAttributeCheck(config, 'podCloud', CharSequence, 'kubernetes')
  mapAttributeCheck(config, 'podImageName', CharSequence, 'fxinnovation/jenkinsk8sslave')
  mapAttributeCheck(config, 'podImageVersion', CharSequence, 'latest')
  mapAttributeCheck(config, 'podName', CharSequence, 'jenkins-slave-linux')
  mapAttributeCheck(config, 'podNamespace', CharSequence, 'default')
  mapAttributeCheck(config, 'podNodeUsageMode', CharSequence, 'NORMAL')
  mapAttributeCheck(config, 'podVolumes', List, [])
  mapAttributeCheck(config, 'preCommitDockerImageName', CharSequence, 'fxinnovation/pre-commit:latest')
  mapAttributeCheck(config, 'runKind', Boolean, false)
  mapAttributeCheck(config, 'slaveSize', CharSequence, 'large')
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

https://github.com/pulls?q=is%3Apr+created%3A%3E%3D2022-03-06+user%3AFXinnovation+is%3Aopen
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
    )
  ]

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
    ],
    xlarge : [
      resourceRequestCpu   : '2000m',
      resourceLimitCpu     : '3',
      resourceRequestMemory: '4096Mi',
      resourceLimitMemory  : '5120Mi',
    ],
    xxlarge : [
      resourceRequestCpu   : '2500m',
      resourceLimitCpu     : '4',
      resourceRequestMemory: '8192Mi',
      resourceLimitMemory  : '10240Mi',
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

  if (config.runKind && !IOC.get('JENKINS_LOCAL')) {
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

  printDebug("Launch without kube: " + IOC.get('JENKINS_LOCAL'))

  if (!IOC.get('JENKINS_LOCAL')) {
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
    config.dockerRegistryLogin,
    config.checkoutCredentialID,
    config.checkoutDirectory,
    config.checkoutRepositoryURL,
    config.checkoutTag,
    config.dockerDataBasepath,
    config.dockerDataIsCurrentDirectory,
    config.dockerRegistry,
    config.dockerRegistryCredentialId,
    config.headerMessage,
    config.preCommitDockerImageName,
    config.defaultBranchName
  )

  timeout(
    time: config.timeoutTime,
    unit: config.timeoutUnit
  ) {
    try {
      DeprecatedFunction deprecatedFunction = IOC.get(DeprecatedFunction.class.getName())

      ansiColor('xterm') {
        stage('prepare') {
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PRE_PREPARE, pipelineEventData)
          if (closureHelper.isDefined('prePrepare')) {
            return deprecatedFunction.execute({
              closureHelper.execute('prePrepare')
            }, 'closure:prePrepare', 'Use Observer system. Create listener listening to PipelineEvents.PRE_PREPARE”.', '01-03-2022')
          }
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PREPARE, pipelineEventData)

          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.POST_PREPARE, pipelineEventData)
          if (closureHelper.isDefined('postPrepare')) {
            return deprecatedFunction.execute({
              closures.postPrepare(IOC.get(ScmInfo.class.getName()))
            }, 'closure:postPrepare', 'Use Observer system. Create listener listening to PipelineEvents.POST_PREPARE”.', '01-03-2022')
          }
        }

        stage('build') {
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PRE_BUILD, pipelineEventData)
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.BUILD, pipelineEventData)
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.POST_BUILD, pipelineEventData)
        }

        stage('pipeline') {
          if (closureHelper.isDefined('prePipeline')) {
            return deprecatedFunction.execute({
              closureHelper.executeWithinStage('prePipeline')
            }, 'closure:prePipeline', 'Use Observer system. Create listener listening to PipelineEvents.PRE_PIPELINE”.', '01-03-2022')
          }

          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PRE_TEST, pipelineEventData)
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.TEST, pipelineEventData)
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.POST_TEST, pipelineEventData)

          closures.pipeline(IOC.get(ScmInfo.class.getName()))

          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PRE_PUBLISH, pipelineEventData)
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.PUBLISH, pipelineEventData)
          pipelineEventData = eventDispatcher.dispatch(PipelineEvents.POST_PUBLISH, pipelineEventData)

          if (closureHelper.isDefined('postPipeline')) {
            return deprecatedFunction.execute({
              closureHelper.executeWithinStage('postPipeline')
            }, 'closure:postPipeline', 'Use Observer system. Create listener listening to PipelineEvents.POST_PIPELINE”.', '01-03-2022')
          }
        }
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
        if (!IOC.get('JENKINS_LOCAL')) {
          cleanWs()
        }
        closureHelper.execute('postCleanup')
      }

      if (config.runKind && !IOC.get('JENKINS_LOCAL')) {
        stage('kindlogs') {
          containerLog(
            name: 'kind'
          )
        }
      }
    }
  }
}
