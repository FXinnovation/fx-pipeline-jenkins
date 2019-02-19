def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
  }
  if (!config.containsKey('cookstyle') || !(config.cookstyle instanceof Map)){
    config.cookstyle = [:]
  }
  if (!config.containsKey('foodcritic') || !(config.foodcritic instanceof Map)){
    config.foodcritic = [:]
  }
  if (!config.containsKey('kitchen') || !(config.kitchen instanceof Map)){
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

  if (closures.containsKey('preTest')){
    stage('pre-test'){
      closures.preTest()
    }
  }
  //stage('test'){
  //  cookstyle(config.cookstyle)
  //  foodcritic(config.foodcritic)
  //  kitchen(config.kitchen)
  //}
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
