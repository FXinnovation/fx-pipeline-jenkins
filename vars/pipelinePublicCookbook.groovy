def call(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('preTest') || !(closures.preTest instanceof Closure)){
    closures.preTest = {}
  }
    println closures.preTest.getClass()
  if (!closures.containsKey('postTest') || !(closures.postTest instanceof Closure)){
    closures.postTest = {}
  }
  if (!closures.containsKey('prePublish') || !(closures.prePublish instanceof Closure)){
    closures.prePublish = {}
  }
  if (!closures.containsKey('postPublish') || !(closures.postPublish instanceof Closure)){
    closures.postPublish = {}
  }
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
  }
  if (!config.containsKey('supermarketCredentialsId')){
    error ('supermarketCredentialsId parameter is mandatory.')
  }

  stage('pre-test'){
    closures.preTest()
  }
  stage('test'){
    cookstyle()
    foodcritic()
    kitchen()
  }
  stage('post-test'){
    closures.postTest()
  }
  stage('pre-publish'){
    closures.prePublish()
  }
  stage('publish'){
    if (config.publish){
      stove(
        credentialsId: config.supermarketCredentialsId
      )
    }else{
      println "Publish step is skipped"
    }
  }
  stage('post-publish'){
    closures.postPublish()
  }
}
