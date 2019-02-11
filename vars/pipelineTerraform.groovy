def call(Map config = [:], Map closures = [:]){
  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a java.lang.Closure.")
    }
  }
  if (!config.containsKey('commandTargets') || !(config.commandTargets instanceof List)){
    config.commandTargets = ['.']
  }

  init(config, closures)

  validate(config, closures)

  test(config, closures)

  publish(config, closures)
}

def init(Map config = [:], Map closures = [:]){
  if (!config.containsKey('initOptions') || !(config.initOptions instanceof Map)){
    config.initOptions = [:]
  }
  if (!closures.containsKey('init')){
    closures.init = {
      for (commandTarget in config.commandTargets) {
        terraform.init([
            commandTarget: commandTarget
          ] + config.initOptions
        )
      }
    }
  }

  if (closures.containsKey('preInit')){
    stage('pre-init'){
      closures.preInit()
    }
  }
  stage('init'){
    closures.init()
  }
  if (closures.containsKey('postInit')){
    stage('post-init'){
      closures.postInit()
    }
  }
}

def validate(Map config = [:], Map closures = [:]){
  if (!config.containsKey('validateOptions') || !(config.validateOptions instanceof Map)){
    config.validateOptions = [:]
  }
  if (!config.containsKey('fmtOptions') || !(config.fmtOptions instanceof Map)){
    config.fmtOptions = [:]
  }
  if (!closures.containsKey('validate')){
    closures.validate = {
      for (commandTarget in config.commandTargets) {
        terraform.validate([
            commandTarget: commandTarget
          ] + config.validateOptions
        )
        terraform.fmt([
            check: true,
            commandTarget: commandTarget,
          ] + config.fmtOptions
        )
      }
    }
  }

  if (closures.containsKey('preValidate')){
    stage('pre-validate'){
      closures.preValidate()
    }
  }
  stage('validate'){
    closures.validate()
  }
  if (closures.containsKey('postValidate')){
    stage('post-validate'){
      closures.postValidate()
    }
  }
}

def test(Map config = [:], Map closures = [:]){
  if (!config.containsKey('testPlanOptions') || !(config.testPlanOptions instanceof Map)){
    config.testPlanOptions = [:]
  }
  if (!config.containsKey('testApplyOptions') || !(config.testApplyOptions instanceof Map)){
    config.testApplyOptions = [:]
  }
  if (!config.containsKey('testDestroyOptions') || !(config.testDestroyOptions instanceof Map)){
    config.testDestroyOptions = [:]
  }
  if (!closures.containsKey('test')){
    closures.test = {
      for (commandTarget in config.commandTargets) {
        try {
          terraform.plan([
              out: 'test.out',
              state: 'test.tfstate',
              commandTarget: commandTarget,
            ] + config.testPlanOptions
          )
          terraform.apply([
              stateOut: 'test.tfstate',
              parallelism: 1,
              refresh: false,
              commandTarget: 'test.out',
            ] + config.testApplyOptions
          )
          replay = terraform.plan([
              out: 'test.out',
              state: 'test.tfstate',
              commandTarget: commandTarget,
            ] + config.testPlanOptions
          )

          if (!(replay.stdout =~ /.*Infrastructure is up-to-date.*/)) {
            error('Replaying the “plan” contains new changes. It is important to make sure terraform consecutive runs make no changes.')
          }
        } catch (errorApply) {
          archiveArtifacts(
            allowEmptyArchive: true,
            artifacts: 'test.tfstat*'
          )
          throw (errorApply)
        } finally {
          terraform.destroy([
              state: 'test.tfstate',
              commandTarget: commandTarget
            ] + config.testDestroyOptions
          )
        }
      }
    }
  }

  if (closures.containsKey('preTest')){
    stage('pre-test'){
      closures.preTest()
    }
  }
  stage('test') {
    closures.test()
  }
  if (closures.containsKey('postTest')){
    stage('post-test'){
      closures.postTest()
    }
  }
}

def publish(Map config = [:], Map closures = [:]){
  if (!config.containsKey('publishPlanOptions') || !(config.publishPlanOptions instanceof Map)){
    config.publishPlanOptions = [:]
  }
  if (!config.containsKey('publishApplyOptions') || !(config.publishApplyOptions instanceof Map)){
    config.publishApplyOptions = [:]
  }
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
  }
  if (!closures.containsKey('publish') && config.publish){
    closures.publish = {
      println "Publish step was not defined."
    }
  }

  if (config.publish) {
    if (closures.containsKey('prePublish')){
      stage('pre-publish'){
        closures.prePublish()
      }
    }
    stage('publish') {
      closures.publish()
    }
    if (closures.containsKey('postPublish')){
      stage('post-publish'){
        closures.postPublish()
      }
    }
  }else{
    println 'Publish step is skipped because "config.publish" is false.'
  }
}
