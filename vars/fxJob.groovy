def call(Map closures = [:], List propertiesConfig = []){
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
  if (!closures.containsKey('prepare') || !(closures.prepare instanceof Closure)){
    closures.prepare = {
      scmInfo = fxCheckout()
      println scmInfo
      return scmInfo
    }
  }
  // It is not possible to name the closure “notify” because a java.lang.Map is an object and every object inherit the
  // notify method. Overriding the Map “notify” method is not a good idea.
  if (!closures.containsKey('notification') || !(closures.notification instanceof Closure)){
    closures.notification = {
      fx_notify(
        status: status
      )
    }
  }
  if (!closures.containsKey('cleanup') || !(closures.cleanup instanceof Closure)){
    closures.cleanup = {
      cleanWs()
    }
  }

  if (!closures.containsKey('pipeline') || !(closures.pipeline instanceof Closure)){
    error('pipeline closure is mandatory.')
  }

  properties(propertiesConfig)
  status='SUCCESS'
  node(){
    try{
      ansiColor('xterm') {
        stage('prepare'){
          closurePrepareReturn = closures.prepare()
          println closurePrepareReturn
        }
        closures.pipeline()
      }
    }catch(error){
       status='FAILURE'
       throw error
    }finally{
      stage('notify'){
        closures.notification()
      }
      stage('cleanup'){
        closures.cleanup()
      }
    }
  }
}
