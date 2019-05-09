def call(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'publish', Boolean, false)
  mapAttributeCheck(config, 'bag', CharSequence, '“databag” parameter is mandatory.')
  mapAttributeCheck(config, 'knifeConfig', Map, '“knifeConfig” parameter is mandatory.')
  mapAttributeCheck(config.knifeConfig, 'commandTarget', CharSequence, '“knifeConfig.commandTarget” parameter is mandatory.')
  mapAttributeCheck(config.knifeConfig, 'secret', CharSequence, '“knifeConfig.secret” parameter is mandatory.')
   
  for (closure in closures){
    if (!closure instanceof Closure){
      error("${closure.key} has to be a Closure")
    }
  }

  if (closures.containsKey('preTest')){
    stage('pre-test'){
      closures.preTest()
    }
  }
  stage('test'){
    databag = readJSON file: config.knifeConfig.commandTarget
  }
  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
  if (closures.containsKey('prePlan')){
    stage('pre-plan'){
      closures.prePlan()
    }
  }
  stage('plan'){
    databagExists = false
    bagExists = false

    databagBagList = readJSON text: knife.databagList(
        serverUrl: config.knifeConfig.serverUrl,
        credentialId: config.knifeConfig.credentialId,
        commandTarget: config.bag,
        format: 'json'
      ).stdout
    databagBagList.each {
      if (config.bag == it){
        bagExists = true
      }
    }
    
    if (true == bagExists) {
      databagItemList = readJSON text: knife.databagShow(
        serverUrl: config.knifeConfig.serverUrl,
        credentialId: config.knifeConfig.credentialId,
        commandTarget: config.bag,
        format: 'json'
      ).stdout
      databagItemList.each {
        if (databag.name == it){
          databagExists = true
        }
      }
    }
    if (true == databagExists){
      println "Databag ${databag.name} exist."
    }else{
      println "Databag ${databag.name} does not exist."
    }
  }
  if (closures.containsKey('postPlan')){
    stage('post-plan'){
      closures.postPlan()
    }
  }
  stage('publish'){
    if (config.publish){
      config.knifeConfig = "${config.bag} ${config.knifeConfig.commandTarget}"
      knife.databagFromFile(config.knifeConfig)
    }else{
      println "Publish step is skipped"
    }
  }
  if (closures.containsKey('postPublish')){
    stage('post-publish'){
      closures.postPublish()
    }
  }
}
