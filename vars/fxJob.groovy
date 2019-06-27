def call(Map closures = [:], List propertiesConfig = []){
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
      [cron('@midnight')]
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
        resourceRequestCpu: '100m',
        resourceLimitCpu: '1',
        resourceRequestMemory: '1024Mi',
        resourceLimitMemory: '2048Mi'
      )
    ]
  ){
    node(label){
      try{
        ansiColor('xterm') {
          stage('prepare'){
            if (closures.containsKey('prePrepare')){
              closures.prePrepare()
            }
            scmInfo = fxCheckout()
            if (closures.containsKey('postPrepare')){
              closures.postPrepare()
            }
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
          fx_notify(
            status: status,
            failOnError: false
          )
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
