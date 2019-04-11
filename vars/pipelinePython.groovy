def call(Map config = [:], Map closures = [:]){
  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a java.lang.Closure.")
    }
  }
  mapAttributeCheck(config, 'version', CharSequence, '3')
  mapAttributeCheck(config, 'folders', List, [])

  stage('Unit Tests') {
    virtualenv(config, closures)
    test(config, closures)
  }

  stage('lint') {
    virtualenv(config, closures)
    lint(config, closures)
  }

//  stage('coverage') {
//    virtualenv(config, closures)
//    coverage(config, closures)
//  }

//  publish(config, closures)
}

static
def virtualenv(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('virtualenv')){
    closures.virtualenv = {
      python.virtualenv([
              version: config.version
      ])
    }
  }
  closures.virtualenv()
}

def test(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('test')){
    closures.test = {
      python.test([
              version: config.version
      ])
    }
  }
  closures.test()
}

def lint(Map config = [:], Map closures = [:]){

  if (!closures.containsKey('lint')){
    closures.lint = {
      for (folder in config.folders) {
        python.lint([
                version: config.version,
                folder : folder
        ])
      }
    }
  }
  closures.lint()
}

