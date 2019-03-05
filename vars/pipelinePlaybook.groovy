def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('ansiblelint') || !(config.ansiblelint instanceof Map)){
    config.ansiblelint = [:]
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
    ansiblelint(config.ansiblelint)
  }

  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
}
