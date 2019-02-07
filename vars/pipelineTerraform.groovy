def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
  }
  if (!config.containsKey('init') || !(config.cookstyle instanceof Map)){
    config.cookstyle = [:]
  }
  if (!config.containsKey('validate') || !(config.foodcritic instanceof Map)){
    config.foodcritic = [:]
  }
  if (!config.containsKey('test') || !(config.kitchen instanceof Map)){
    config.kitchen = [:]
  }
  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a Closure")
    }
  }
  if (!closures.containsKey('publish') && config.publish){
    closures.publish = {
      println "Publishing step was not defined"
    }
  }

  if (closures.containsKey('preValidate')){
    stage('pre-validate'){
      closures.preValidate()
    }
  }
  stage('validate'){
  }
  if (closures.containsKey('postValidate')){
    stage('post-validate'){
      closures.postValidate()
    }
  }

  if (closures.containsKey('preInit')){
    stage('pre-init'){
      closures.preInit()
    }
  }
  stage('init'){
  }
  if (closures.containsKey('postInit')){
    stage('post-init'){
      closures.postInit()
    }
  }

  if (closures.containsKey('preTest')){
    stage('pre-test'){
      closures.preTest()
    }
  }
  stage('test'){
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
      closures.publish()
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
