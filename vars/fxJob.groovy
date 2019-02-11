def call(Map closures = [:], Map propertiesConfig = []){
  if ([] == propertiesConfig){
    propertiesConfig = [
      buildDiscarder(
        logRotator(
          artifactDaysToKeepStr: '',
          artifactNumToKeepStr: '10',
          daysToKeepStr: '',
          numToKeepStr: '10'
        )
      ),
      pipelineTriggers(
        [[
          $class: 'PeriodicFolderTrigger',
          interval: '1d'
        ]]
      )
    ]
  }
  if (!closures.containsKey('prepare')){
    closures.prepare = {
      scmInfo = fxCheckout()
    }
  }
  if (!closures.containsKey('notify')){
    closures.notify = {
      fx_notify(
        status: status
      )
    }
  }
  if (!closures.containsKey('cleanup')){
    closures.cleanup = {
      cleanWs()
    }
  }

  if (!closures.containsKey('pipeline')){
    error('pipeline closure is mandatory.')
  }

  properties(propertiesConfig)
  status='SUCCESS'
  node(){
    try{
      stage('prepare'){
        closures.prepare()
      }
      closures.pipeline()
    }catch(error){
       status='FAILURE'
       throw error
    }finally{
      stage('notify'){
        closures.notify()
      }
      stage('cleanup'){
        closures.cleanup()
      }
    }
  }
}
