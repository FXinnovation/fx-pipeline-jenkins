import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  mapAttributeCheck(config, 'deploy', Boolean, false)

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preTest')

  stage('test'){
    helmTestConfig = config.helmConfig.clone()
    helmTestConfig.dryRun = true
    helm.upgrade(helmTestConfig)
  }

  closureHelper.executeWithinStage('postTest')
  closureHelper.executeWithinStage('preDeploy')

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

  closureHelper.executeWithinStage('postDeploy')
}
