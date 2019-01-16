def call (Map config = [:]){
  properties([
    buildDiscarder(
      logRotator(
        artifactDaysToKeepStr: '10',
        artifactNumToKeepStr: '10',
        daysToKeepStr: '10',
        numToKeepStr: '10'
      )
    ),
    pipelineTriggers([[
      $class: 'PeriodicFolderTrigger',
      interval: '1d']])
  ])
  
  node(){
    try{
      if (!config.containsKey('application')){
        error("Please specify the application of the job!")
      }
      stage('pre-build'){
        scmInfo = fx_checkout()
        sh 'kubectl --version'
      }
      stage('validate'){
        sh "kubectl apply --dry-run=true -R -f ./"
      }
      stage('deploy'){
        if(scmInfo.tag != ''){
          fx_notify(
            status: 'IN PROGRESS'
            notifiedPeople: '@all '
            message: "${config.application} will be redeployed in one minute! Some downtime might be expected!"
          )
          sleep(
            time: 1,
            unit: 'MINUTES'
          )
          sh "kubectl apply -R -f ./"
        }else{
          println 'This is not a released version, skipping deployment'
        }
      }
    }catch(error){
      result="FAILURE"
      throw(error)
    }finally{
      stage('notify - result'){
        fx_notify(
          status: result
        )
      }
      stage('cleaning'){
        cleanWs()
      }
    }
  }
}
