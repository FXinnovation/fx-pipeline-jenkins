def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
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
    packer.validate(config.validateConfig)
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
      packer.build(config.buildConfig)
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
