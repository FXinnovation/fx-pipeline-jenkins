import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'publish', Boolean, false)
  mapAttributeCheck(config, 'bag', CharSequence, '“databag” parameter is mandatory.')
  mapAttributeCheck(config, 'knifeConfig', Map, '“knifeConfig” parameter is mandatory.')
  mapAttributeCheck(config.knifeConfig, 'commandTarget', CharSequence, '“knifeConfig.commandTarget” parameter is mandatory.')
  mapAttributeCheck(config, 'secretId', CharSequence, '“knifeConfig.secretId” parameter is mandatory.')
  
  closureHelper = new ClosureHelper(this, closures)   

  closureHelper.executeWithinStage('preTest')
  
  stage('test'){
    databag = readJSON(file: config.knifeConfig.commandTarget)
  }

  closureHelper.executeWithinStage('preTest')
  closureHelper.executeWithinStage('prePlan')

  stage('plan'){
    databagExists = false
    bagExists = false

    databagBagList = readJSON(text: knife.databagList(
        serverUrl: config.knifeConfig.serverUrl,
        credentialId: config.knifeConfig.credentialId,
        commandTarget: config.bag,
        format: 'json'
      ).stdout
    )
    databagBagList.each {
      if (config.bag == it){
        bagExists = true
      }
    }
    
    if (true == bagExists) {
      databagItemList = readJSON(text: knife.databagShow(
          serverUrl: config.knifeConfig.serverUrl,
          credentialId: config.knifeConfig.credentialId,
          commandTarget: config.bag,
          format: 'json'
        ).stdout
      )
      databagItemList.each {
        if (databag.id == it){
          databagExists = true
        }
      }
    }
    if (true == bagExists) {
      println "Bag ${config.bag} exist. Nothing to do"
    }else {
      println "Bag ${config.bag} does not exist. Need to create it first."
    }
    if (true == databagExists){
      println "Item ${databag.id} exist. This will be updated."
    }else{
      println "Item ${databag.id} does not exist. This will be created."
    }
  }

  closureHelper.executeWithinStage('postPlan')
  closureHelper.executeWithinStage('prePublish')

  stage('publish'){
    if (config.publish){
      if (false == bagExists) {
        println "Creating bag ${config.bag} first ..."
        configBag = [
          'credentialId': config.knifeConfig.credentialId,
          'serverUrl': config.knifeConfig.serverUrl,
          'commandTarget': config.bag,
        ]
        knife.databagCreateBag(configBag) 
      }
      withCredentials([
        string(
          credentialsId: config.secretId,
          variable: 'tmpSecretName',
        )
      ]) {
        config.knifeConfig.commandTarget = "${config.bag} ${config.knifeConfig.commandTarget}"
        config.knifeConfig.secret = tmpSecretName
        knife.databagFromFile(config.knifeConfig)
      }
    }else{
      println "Publish step is skipped"
    }
  }

  closureHelper.executeWithinStage('postPublish')
}
