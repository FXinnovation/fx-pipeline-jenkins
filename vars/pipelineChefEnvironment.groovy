import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'publish', Boolean, false)
  mapAttributeCheck(config, 'knifeConfig', Map, [:], '“knifeConfig” parameter is mandatory.')
  mapAttributeCheck(config.knifeConfig, 'commandTarget', CharSequence, "", '“knifeConfig.commandTarget” parameter is mandatory.')

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preTest')
  stage('test'){
    environment = readJSON(file: config.knifeConfig.commandTarget)
    // TODO: We will need to make some additionnal validation here
    // for the time being, we only validate it's valid json. In the future, we need to be able to check
    // if every cookbook available on the chef-server is pinned.
  }

  closureHelper.executeWithinStage('postTest')
  closureHelper.executeWithinStage('prePlan')

  stage('plan'){
    environmentExists = false
    environmentList = readJSON(text: knife.environmentList(
        serverUrl: config.knifeConfig.serverUrl,
        credentialId: config.knifeConfig.credentialId,
        format: 'json'
      ).stdout
    )
    environmentList.each {
      if (environment.name == it){
        environmentExists = true
      }
    }
    if (true == environmentExists){
      currentEnvironment = knife.environmentShow(
        serverUrl: config.knifeConfig.serverUrl,
        credentialId: config.knifeConfig.credentialId,
        commandTarget: environment.name,
        format: 'json'
      ).stdout
      writeFile(file: 'currentEnv.json', text: currentEnvironment)
      execute(script: "git diff --no-index --color currentEnv.json ${config.knifeConfig.commandTarget}", throwError: false)
    }else{
      println 'Environment does not exist, this will be created.'
      execute(script: "cat ${config.knifeConfig.commandTarget}")
    }
  }

  closureHelper.executeWithinStage('postPlan')
  closureHelper.executeWithinStage('prePublish')

  stage('publish'){
    if (config.publish){
      knife.environmentFromFile(config.knifeConfig)
    }else{
      println "Publish step is skipped"
    }
  }

  closureHelper.executeWithinStage('postPublish')
}
