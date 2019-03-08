def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('ansiblelintConfig')) {
    config.ansiblelintConfig = [:]
  } else if (!(config.ansiblelintConfig instanceof Map)) {
    error('ansiblelintConfig parameter must be of type Map')
  }

  if (!config.containsKey('ansiblelintOutputFile')) {
    config.ansiblelintOutputFile = 'ansible-lint.txt'
  } else if (!(config.ansiblelintOutputFile instanceof String)) {
    error('ansiblelintOutputFile parameter must be of type String')
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
        file: config.ansiblelintOutputFile,
        text: error.getMessage()
      )
      archiveArtifacts(
        artifacts: config.ansiblelintOutputFile
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
