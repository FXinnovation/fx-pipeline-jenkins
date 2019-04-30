def call(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')

  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a java.lang.Closure.")
    }
  }

  stage('test “' + config.commandTarget + '”') {
    lint(config, closures)
    if (fileExists('requirements.yml')) {
    }
    converge(config, closures)
    test(config, closures)
  }

  publish(config, closures)
}


def lint(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'lintOptions',    CharSequence, '-p --parseable-severity')
  mapAttributeCheck(config, 'lintOutputFile', CharSequence, 'ansible-lint.txt')

  if (!closures.containsKey('lint')){
    closures.lint = {
      try {
        ansibleLint(
          options: config.lintOptions,
          commandTarget: config.commandTarget
        )
      } catch(error) {
        writeFile(
          file: config.lintOutputFile,
          text: error.getMessage()
        )
        archiveArtifacts(
          artifacts: config.lintOutputFile
        )

        throw(error)
      }
    }
  }

  if (closures.containsKey('preLint')){
    closures.preLint()
  }

  closures.lint()

  if (closures.containsKey('postLint')){
    closures.postLint()
  }
}

def converge(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'convergeOptions', Map, [:])

  println('No Ansible “converge” step define yet.')
}

def test(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'testOptions', Map, [:])

  println('No Ansible “test” step define yet.')
}

def publish(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'publish', Boolean, false)

  if (config.publish) {
    stage('publish') {
      if (closures.containsKey('prePublish')) {
        closures.prePublish()
      }

      closures.publish()

      if (closures.containsKey('postPublish')) {
        closures.postPublish()
      }
    }
  }else{
    println('Publish step is skipped because "config.publish" is false.')
  }
}
