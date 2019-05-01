import OptionString

def validate(Map config = [:]){
  config.subCommand = 'validate'
  validParameters = [
    'checkVariables':'',
    'noColor':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def refresh(Map config = [:]){
  config.subCommand = 'refresh'
  validParameters = [
    'backup':'',
    'lock':'',
    'lockTimeout':'',
    'noColor':'',
    'state':'',
    'stateOut':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def slowRefresh(Map config = [:]){
  config.subCommand = 'refresh'
  validParameters = [
    'backup':'',
    'lock':'',
    'lockTimeout':'',
    'noColor':'',
    'state':'',
    'stateOut':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraformFiles = findFiles(glob: '*.tf')
  config.targets = []
  for ( terraformFile in terraformFiles ) {
    currentResources = readJSON text: sh(
      returnStdout: true,
      script: "cat ${terraformFile.toString()} | docker run --rm -i fxinnovation/json2hcl -reverse"
    )
    for ( resource in currentResources.resource ){
      currentResourceType = resource.keySet().toArray()[0]
      for ( tfResource in resource."${currentResourceType}") {
        currentResourceId = tfResource.keySet().toArray()[0]
        config.targets[config.targets.size()] = "'${currentResourceType}.${currentResourceId}'"
        if ( config.targets.size() >= 5 ){
          terraform(config)
          config.targets = []
          sh 'sleep 1'
        }
      }
    }
    terraform(config)
  }
}

def init(Map config = [:]){
  config.subCommand = 'init'
  validParameters = [
    'backend':'',
    'backendConfigs':'',
    'forceCopy':'',
    'fromModule':'',
    'get':'',
    'getPlugins':'',
    'lock':'',
    'lockTimeout':'',
    'noColor':'',
    'pluginDirs':'',
    'reconfigure':'',
    'upgrade':'',
    'verifyPlugins':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def plan(Map config = [:]){
  config.subCommand = 'plan'
  validParameters = [
    'destroy':'',
    'lock':'',
    'lockTimeout':'',
    'moduleDepth':'',
    'noColor':'',
    'out':'',
    'parallelism':'',
    'refresh':'',
    'state':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  config.input=false
  terraform(config)
}

def apply(Map config = [:]){
  config.subCommand = 'apply'
  validParameters = [
    'backup':'',
    'lock':'',
    'lockTimeout':'',
    'noColor':'',
    'parallelism':'',
    'refresh':'',
    'state':'',
    'stateOut':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  config.autoApprove=true
  config.input=false
  terraform(config)

}

def destroy(Map config = [:]){
  config.subCommand = 'destroy'
  validParameters = [
    'backup':'',
    'lock':'',
    'lockTimeout':'',
    'noColor':'',
    'parallelism':'',
    'refresh':'',
    'state':'',
    'stateOut':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  config.force = true
  terraform(config)
}

def fmt(Map config = [:]){
  config.subCommand = 'fmt'
  validParameters = [
    'list':'',
    'write':'',
    'diff':'',
    'check':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/terraform:latest')
  mapAttributeCheck(config, 'subCommand', CharSequence, '', 'ERROR: The subcommand must be defined!')
  mapAttributeCheck(config, 'dockerAdditionalMounts', Map, [:])
  mapAttributeCheck(config, 'dockerEnvironmentVariables', Map, [:])
  mapAttributeCheck(config, 'commandTarget', CharSequence, '')

  optionsString = new OptionString()
  optionsString.setDelimiter('=')

  if ( config.containsKey('backend') ){
    optionsString.add('-backend', config.backend, Boolean)
  }
  if ( config.containsKey('check') ){
    optionsString.add('-check', config.check, Boolean)
  }
  if ( config.containsKey('list') ){
    optionsString.add('-list', config.list, Boolean)
  }
  if ( config.containsKey('diff') ){
    optionsString.add('-diff', config.diff, Boolean)
  }
  if ( config.containsKey('write') ){
    optionsString.add('-write', config.write, Boolean)
  }
  if ( config.containsKey('backendConfigs') ){
    optionsString.add('-backend-config', config.backendConfigs, ArrayList)
  }
  if ( config.containsKey('backup') ){
    optionsString.add('-backup', config.backup)
  }
  if ( config.containsKey('checkVariables') ){
    optionsString.add('-check-variables', config.checkVariables, Boolean)
  }
  if ( config.containsKey('force') && config.force ){
    optionsString.add('-force', '', Boolean)
  }
  if ( config.containsKey('forceCopy') && config.forceCopy ){
    optionsString.add('-force-copy', '', Boolean)
  }
  if ( config.containsKey('fromModule') ){
    optionsString.add('-from-module', config.fromModule)
  }
  if ( config.containsKey('get') ){
    optionsString.add('-get', config.get, Boolean)
  }
  if ( config.containsKey('getPlugins') ){
    optionsString.add('-get-plugins', config.getPlugins, Boolean)
  }
  if ( config.containsKey('input') ){
    // NOTE: Since this is jenkins executing it, if input has been set, it must
    // be forced set to false.
    optionsString.add('-input', 'false')
  }
  if ( config.containsKey('lock') ){
    optionsString.add('-lock', config.lock, Boolean)
  }
  if ( config.containsKey('lockTimeout') ){
    optionsString.add('-lock-timeout', config.lockTimeout)
  }
  if ( config.containsKey('moduleDepth') ){
    optionsString.add('-module-depth', config.moduleDepth, Integer)
  }
  if ( config.containsKey('noColor') && config.noColor ){
    optionsString.add('-no-color', '', Boolean)
  }
  if ( config.containsKey('out') ){
    optionsString.add('-out', config.out)
  }
  if ( config.containsKey('parallelism') ){
    optionsString.add('-parallelism', config.parallelism, Integer)
  }
  if ( config.containsKey('pluginDirs') ){
    optionsString.add('-plugin-dir', config.pluginDirs, ArrayList)
  }
  if ( config.containsKey('reconfigure') && config.reconfigure ){
    optionsString.add('-lock', '', Boolean)
  }
  if ( config.containsKey('refresh') ){
    optionsString.add('-refresh', config.refresh, Boolean)
  }
  if ( config.containsKey('state') ){
    optionsString.add('-state', config.state)
  }
  if ( config.containsKey('stateOut') ){
    optionsString.add('-state-out', config.state)
  }
  if ( config.containsKey('targets') ){
    optionsString.add('-targets', config.targets, ArrayList)
  }
  if ( config.containsKey('upgrade') ){
    optionsString.add('-upgrade', config.upgrade, Boolean)
  }
  if ( config.containsKey('varFile') ){
    optionsString.add('-var-file', config.varFile)
  }
  if ( config.containsKey('vars') ){
    optionsString.add('-var', config.vars, ArrayList)
  }
  if ( config.containsKey('verifyPlugins') ){
    optionsString.add('-verify-plugins', config.verifyPlugins, Boolean)
  }

  terraformCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand:  'terraform',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables,
  )

  execute(
    script: "${terraformCommand} version"
  )

  return execute(
    script: "${terraformCommand} ${config.subCommand} ${optionsString} ${config.commandTarget}"
  )
}
