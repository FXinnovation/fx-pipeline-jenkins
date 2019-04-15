def call(Map closures = [:]){
  status='SUCCESS'
  def label = UUID.randomUUID().toString()
  printDebug(label)
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
      }
    }
  }
}