def call(Map config = [:], Map closures = [:]){
  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a java.lang.Closure.")
    }
  }
  mapAttributeCheck(config, 'publish', Boolean, false)

  if (closures.containsKey('preInstall')){
    stage('pre-install'){
      closures.preInstall()
    }
  }
  stage('install') {
    yarn.install()
  }
  if (closures.containsKey('postInstall')){
    stage('post-install'){
      closures.postInstall()
    }
  }
  if (closures.containsKey('preTest')){
    stage('pre-test'){
      closures.preTest()
    }
  }
  stage('test') {
    yarn.test()
  }
  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
  if (closures.containsKey('preAudit')){
    stage('pre-audit'){
      closures.preAudit()
    }
  }
  stage('audit') {
    yarn.audit()
  }
  if (closures.containsKey('postAudit')){
    stage('post-audit'){
      closures.postAudit()
    }
  }
}
