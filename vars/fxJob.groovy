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
        [cron('@midnight')]
      )
    ]
  }
  closures.each { name, closure
    if (!(closure instanceof Closure)){
      error("${name} must be of type Closure")
    }
  }

  if (!closures.containsKey('pipeline')){
    error('pipeline closure is mandatory.')
  }

  properties(propertiesConfig)
  status='SUCCESS'
  node(){
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
          status: status
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
