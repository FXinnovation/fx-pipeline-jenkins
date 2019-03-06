def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('ansiblelintConfig')) {
    config.ansiblelintConfig = [:]
  } else if (!(config.ansiblelintConfig instanceof Map)) {
    error('ansiblelintConfig parameter must be of type Map')
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
    try {
      ansiblelint(config.ansiblelintConfig)
    } catch(error) {
      writeFile(
        file: 'ansible-lint.txt',
        text: error.getMessage()
      )
      throw(error)
    }
  }

  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
}
