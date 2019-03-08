def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('deploy') || !(config.deploy instanceof Boolean)){
    config.deploy = false
  }
  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a Closure")
    }
  }

  if (closures.containsKey('preTest')){
    stage('pre-test'){
      closures.preTest()
    }
  }
  stage('test'){
    helmTestConfig = config.helmConfig.clone()
    helmTestConfig.dryRun = true
    helm.upgrade(helmTestConfig)
  }
  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
  if (closures.containsKey('preDeploy')){
    stage('pre-deploy'){
      closures.preDeploy()
    }
  }
  stage('deploy'){
    try {
      if (true == config.deploy){
        return helm.upgrade(config.helmConfig)
      }else{
        println 'Deploy stage has been skipped'
      }
    }catch(error){
      try{
        releases = readJSON text: helm.list(
          output: 'json',
          failed: true,
          commandTarget: "^${config.helmConfig.release}\$"
        ).stdout
        if ( 1 != releases.Releases.size() || 1 == releases.Releases[0].Revision){
          println 'I cannot determine which release to rollback, or this is an initial deployment.'
        }else{
          helm.rollback(
            force: true,
            release: config.helmConfig.release,
            revision: releases.Releases[0].Revision - 1,
            wait: true
          )
        }
      }catch(errorHandler){}
      throw error
    }
  }
  if (closures.containsKey('postDeploy')){
    stage('post-deploy'){
      closures.postDeploy()
    }
  }
}
