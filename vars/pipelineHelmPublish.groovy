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
    lintConfig = [
      commandTarget = config.chartName
    ]
    helm.lint(lintConfig)
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
    if (true == config.deploy){
      return helm.publish([
        chartFolder: config.chartName,
        repo: config.repo
      ])
    }else{
      println 'Publish stage has been skipped'
    }
  }
  if (closures.containsKey('postPublish')){
    stage('post-publish'){
      closures.postPublish()
    }
  }
}
