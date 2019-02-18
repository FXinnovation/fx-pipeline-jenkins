def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
  }
  if (!config.containsKey('knifeConfig') || !(config.knifeConfig instanceof Map)){
    error('knifeConfig parameter is mandatory and must be of type Map')
  }
  if (!config.knifeConfig.containsKey('commandTarget') || !(config.knifeConfig.commandTarget instanceof String)){
    error('knifeConfig.commandTarget is mandatory and must be of type String')
  }
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
    environment = readJSON file: config.knifeConfig.commandTarget
    // TODO: We will need to make some additionnal validation here
    // for the time being, we only validate it's valid json. In the future, we need to be able to check
    // if every cookbook available on the chef-server is pinned.
  }
  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
  if (closures.containsKey('prePublish')){
    stage('pre-publish'){
      closures.prePublish()
    }
  }
  stage('publish'){
    if (config.publish){
      knife.environmentFromFile(config.knifeConfig)
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
