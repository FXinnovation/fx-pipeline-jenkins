def call(Map config = [:], Map closures = [:]){
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
  }
  if (!config.containsKey('knifeConfig') || !(config.knifeConfig instanceof Map)){
    error('knifeConfig parameter is mandatory and must be of type Map')
  }
  if (!config.knifeConfig.containsKey('commandTarget') || !(config.knifeConfig.commandTarget instanceof CharSequence)){
    error('knifeConfig.commandTarget is mandatory and must be of type String')
  }
  for (closure in closures){
    if (!closure instanceof Closure){
      error("${closure.key} has to be a Closure")
    }
  }

  if (closures.containsKey('preTest')){
    stage('pre-test'){
      closures.preTest()
    }
  }
  stage('test'){
    environment = readJSON file: config.knifeConfig.commandTarget
    // TODO: We will need to make some additionnal validation here
    // for the time being, we only validate it's valid json. In the future, we need to be able to check
    // if every cookbook available on the chef-server is pinned.
  }
  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
  if (closures.containsKey('prePlan')){
    stage('pre-plan'){
      closures.prePlan()
    }
  }
  stage('plan'){
    environmentExists = false
    environmentList = readJSON text: knife.environmentList(
      serverUrl: config.knifeConfig.serverUrl,
      credentialId: config.knifeConfig.credentialId,
      format: 'json'
    ).stdout
    environmentList.each {
      if (environment.name == it){
        environmentExists = true
      }
    }
    if (true == environmentExists){
      currentEnvironment = knife.environmentShow(
        serverUrl: config.knifeConfig.serverUrl,
        credentialId: config.knifeConfig.credentialId,
        commandTarget: environment.name,
        format: 'json'
      ).stdout
      writeFile file: 'currentEnv.json', text: currentEnvironment
      execute(
        script: "diff -U 10 currentEnv.json ${config.knifeConfig.commandTarget}"
      )
    }else{
      println 'Environment does not exist, this will be created.'
      execute(
        script: "cat ${config.knifeConfig.commandTarget}"
      )
    }
  }
  if (closures.containsKey('postPlan')){
    stage('post-plan'){
      closures.postPlan()
    }
  }
  if (closures.containsKey('prePublish')){
    stage('pre-publish'){
      closures.prePublish()
    }
  }
  stage('publish'){
    if (config.publish){
      knife.environmentFromFile(config.knifeConfig)
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
