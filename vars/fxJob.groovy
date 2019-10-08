def call(Map closures = [:], List propertiesConfig = [], Map config = [:]){
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

  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a Closure")
    }
  }

  if (!closures.containsKey('pipeline')){
    error('pipeline closure is mandatory.')
  }
  mapAttributeCheck(config, 'timeoutTime', Integer, 10)
  mapAttributeCheck(config, 'timeoutUnit', CharSequence, 'HOURS')
  mapAttributeCheck(config, 'slaveSize', CharSequence, 'small')
  mapAttributeCheck(config, 'preCommitDockerImageName', CharSequence, 'fxinnovation/pre-commit:latest')


  def slaveSizes = [
    small: [
      resourceRequestCpu: '100m',
      resourceLimitCpu: '500m',
      resourceRequestMemory: '1024Mi',
      resourceLimitMemory: '1512Mi',
    ],
    medium: [
      resourceRequestCpu: '500m',
      resourceLimitCpu: '1',
      resourceRequestMemory: '1512Mi',
      resourceLimitMemory: '2048Mi',
    ],
    large: [
      resourceRequestCpu: '1500m',
      resourceLimitCpu: '2',
      resourceRequestMemory: '2048Mi',
      resourceLimitMemory: '3072Mi',
    ]
  ]

  def chosenSlaveSize = slaveSizes[config.slaveSize]

  properties(defaultPropertiesConfig + propertiesConfig)
  status='SUCCESS'
  def label = UUID.randomUUID().toString()
  podTemplate(
    cloud: 'kubernetes',
    name:  'jenkins-slave-linux',
    namespace: 'default',
    nodeUsageMode: 'NORMAL',
    idleMinutes: 0,
    slaveConnectTimeout: 100,
    podRetention: never(),
    label: label,
    containers: [
      containerTemplate(
        name: 'jnlp',
        image: "fxinnovation/jenkinsk8sslave:latest",
        args: '${computer.jnlpmac} ${computer.name}',
        privileged: true,
        alwaysPullImage: true,
        workingDir: '/home/jenkins',
        resourceRequestCpu: chosenSlaveSize.resourceRequestCpu,
        resourceLimitCpu: chosenSlaveSize.resourceLimitCpu,
        resourceRequestMemory: chosenSlaveSize.resourceRequestMemory,
        resourceLimitMemory: chosenSlaveSize.resourceLimitMemory
      )
    ]
  ){
    node(label){
      timeout(
        time: config.timeoutTime,
        unit: config.timeoutUnit
      ){
        ansiColor('xterm') {
          println "\u001b[35m"
          println """
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
    """
          println"\u001B[0m"
        }
        try{
          ansiColor('xterm') {
            stage('prepare'){
              if (closures.containsKey('prePrepare')){
                closures.prePrepare()
              }
              scmInfo = fxCheckout()
              if (closures.containsKey('postPrepare')){
                closures.postPrepare(scmInfo)
              }
            }

            if (fileExists('.pre-commit-config.yaml') || fileExists('.pre-commit-config.yml')) {
              preCommitCommand = dockerRunCommand(
                dockerImage: config.preCommitDockerImageName,
                fallbackCommand: 'pre-commit',
                command: 'run -a --color=always',
              )

              execute(
                script: "${preCommitCommand}"
              )
            }
            closures.pipeline(scmInfo)
          }
        }catch(error){
           status='FAILURE'
           throw error
        }finally{
          stage('notify'){
            if (closures.containsKey('preNotify')){
              closures.preNotify()
            }
            if (closures.containsKey('notify')){
              closures.notify()
            }else{
              fx_notify(
                status: status,
                failOnError: false
              )
            }
            if (closures.containsKey('postNotify')){
              closures.postNotify()
            }
          }
          stage('cleanup'){
            if (closures.containsKey('preCleanup')){
              closures.preCleanup()
            }
            cleanWs()
            if (closures.containsKey('postCleanup')){
              closures.postCleanup()
            }
          }
        }
      }
    }
  }
}
