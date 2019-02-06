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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
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
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"fmt\", please remove it!")
    }
  }
  terraform(config)
}

def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = "fxinnovation/terraform:latest"
  }
  if ( !config.containsKey('subCommand') ){
    error('ERROR: The subcommand must be defined!')
  }
  if ( !config.containsKey('dockerAdditionalMounts') ){
    config.dockerAdditionalMounts = []
  }
  if ( !config.containsKey('dockerEnvironmentVariables') ){
    config.dockerEnvironmentVariables = []
  }

  optionsString = ''
  // backend
  if ( config.containsKey('backend') ){
    if ( config.backend instanceof Boolean ){
      optionsString = optionsString + "-backend=${config.backend} "
    }else{
      error('terraform - "backend" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('check') ){
    if ( config.check instanceof Boolean ){
      optionsString = optionsString + "-check=${config.check} "
    }else{
      error('terraform - "check" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('list') ){
    if ( config.list instanceof Boolean ){
      optionsString = optionsString + "-list=${config.list} "
    }else{
      error('terraform - "list" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('diff') ){
    if ( config.diff instanceof Boolean ){
      optionsString = optionsString + "-diff=${config.diff} "
    }else{
      error('terraform - "diff" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('write') ){
    if ( config.write instanceof Boolean ){
      optionsString = optionsString + "-write=${config.write} "
    }else{
      error('terraform - "write" parameter must be of type "Boolean"')
    }
  }
  // backendConfigs
  if ( config.containsKey('backendConfigs') ){
    if ( !config.backendConfigs instanceof ArrayList ){
      error('terraform - "backendConfigs" parameter must be of type "String[]"')
    }
    for (i=0; i<config.backendConfigs.size(); i++){
      optionsString = optionsString + "-backend-config=${config.backendConfig[i]} "
    }
  }
  // backup
  if ( config.containsKey('backup') ){
    if ( config.backup instanceof String ){
      optionsString = optionsString + "-backup=${config.backup} "
    }else{
      error('terraform - "backup" parameter must be of type "String"')
    }
  }
  // checkVariables
  if ( config.containsKey('checkVariables') ){
    if ( config.checkVariables instanceof Boolean ){
      optionsString = optionsString + "-check-variables=${config.checkVariables} "
    }else{
      error('terraform - "checkVariables" parameter must be of type "Boolean"')
    }
  }
  // commandTarget
  if ( config.containsKey('commandTarget') ){
    if ( !config.commandTarget instanceof String ){
      error('terraform - "commandTarget" parameter must be of type "String"')
    }
  }else{
    config.commandTarget = ''
  }
  // force
  if ( config.containsKey('force') ){
    if ( config.force instanceof Boolean ){
      if ( config.force ){
        optionsString = optionsString + "-force "
      }
    }else{
      error('terraform - "force" parameter must be of type "Boolean"')
    }
  }
  // forceCopy
  if ( config.containsKey('forceCopy') ){
    if ( config.forceCopy instanceof Boolean ){
      if ( config.forceCopy ){
        optionsString = optionsString + "-force-copy "
      }
    }else{
      error('terraform - "forceCopy" parameter must be of type "Boolean"')
    }
  }
  // fromModule
  if ( config.containsKey('fromModule') ){
    if ( config.fromModule instanceof String) {
      optionsString = optionsString + "-from-module=${config.fromModule} "
    }else{
      error('terraform - "fromModule" parameter must be of type "String"')
    }
  }
  // get
  if ( config.containsKey('get') ){
    if ( config.get instanceof Boolean ){
      optionsString = optionsString + "-get=${config.get} "
    }else{
      error('terraform - "get" parameter must be of type "Boolean"')
    }
  }
  // getPlugins
  if ( config.containsKey('getPlugins') ){
    if ( config.getPlugins instanceof Boolean ){
      optionsString = optionsString + "-get-plugins=${config.getPlugins} "
    }else{
      error('terraform - "getPlugins" parameter must be of type "Boolean"')
    }
  }
  // input
  // NOTE: Since this is jenkins executing it, if input has been set, it must
  // be set to false.
  if ( config.containsKey('input') ){
    optionsString = optionsString + "-input=false "
  }
  // lock
  if ( config.containsKey('lock') ){
    if ( config.lock instanceof Boolean ){
      optionsString = optionsString + "-lock=${config.lock} "
    }else{
      error('terraform - "lock" parameter must be of type "Boolean"')
    }
  }
  // lockTimeout
  if ( config.containsKey('lockTimeout') ){
    if ( config.lockTimeout instanceof String ){
      optionsString = optionsString + "-lock-timeout=${config.lockTimeout} "
    }else{
      error('terraform - "lockTimeout" parameter must be of type "String"')
    }
  }
  // moduleDepth
  if ( config.containsKey('moduleDepth') ){
    if ( config.moduleDepth instanceof Integer ){
      optionsString = optionsString + "-module-depth=${config.moduleDepth} "
    }else{
      error('terraform - "moduleDepth" parameter must be of type "Integer"')
    }
  }
  // noColor
  if ( config.containsKey('noColor') ){
    if ( config.noColor instanceof Boolean ){
      if ( config.noColor ){
        optionsString = optionsString + "-no-color "
      }
    }else{
      error('terraform - "noColor" parameter must be of type "Boolean"')
    }
  }
  // out
  if ( config.containsKey('out') ){
    if ( config.out instanceof String ){
      optionsString = optionsString + "-out=${config.out} "
    }else{
      error('terraform - "out" parameter must be of type "String"')
    }
  }
  // parallelism
  if ( config.containsKey('parallelism') ){
    if ( config.parallelism instanceof Integer ){
      optionsString = optionsString + "-parallelism=${config.parallelism} "
    }
  }
  // pluginDirs
  if ( config.containsKey('pluginDirs') ){
    if ( !config.pluginDirs instanceof ArrayList ){
      error('terraform - "pluginDirs" parameter must be of type "String[]"')
    }
    for (i=0; i<config.pluginDirs.size(); i++){
      optionsString = optionsString + "-plugin-dir ${config.pluginDirs[i]} "
    }
  }
  // reconfigure
  if ( config.containsKey('reconfigure') ){
    if ( config.reconfigure instanceof Boolean ){
      if ( config.reconfigure ){
        optionsString = optionsString + "-reconfigure "
      }
    }else{
      error('terraform - "reconfigure" parameter must be of type "Boolean"')
    }
  }
  // refresh
  if ( config.containsKey('refresh') ){
    if ( config.refresh instanceof Boolean ){
      optionsString = optionsString + "-refresh=${config.refresh} "
    }else{
      error('terraform - "refresh" parameter must be of type "Boolean"')
    }
  }
  // state
  if ( config.containsKey('state') ){
    if ( config.state instanceof String ){
      optionsString = optionsString + "-state=${config.state} "
    }else{
      error('terraform - "state" parameter must be of type "String"')
    }
  }
  // stateOut
  if ( config.containsKey('stateOut') ){
    if ( config.stateOut instanceof String ){
      optionsString = optionsString + "-state-out=${config.stateOut} "
    }else{
      error('terraform - "stateOut" parameter must be of type "String"')
    }
  }
  // targets
  if ( config.containsKey('targets') ){
    if ( !config.targets instanceof ArrayList ){
      error('terraform - "targets" parameter must be of type "String[]"')
    }
    for (i=0; i<config.targets.size(); i++){
      optionsString = optionsString + "-target=${config.targets[i]} "
    }
  }
  // upgrade
  if ( config.containsKey('upgrade') ){
    if ( config.upgrade instanceof Boolean ){
      optionsString = optionsString + "-upgrade=${config.upgrade} "
    }else{
      error('terraform - "upgrade" parameter must be of type "Boolean"')
    }
  }
  // varFile
  if ( config.containsKey('varFile') ){
    if ( config.varFile instanceof String ){
      optionsString = optionsString + "-var-file=${config.varFile} "
    }else{
      error('terraform - "varFile" parameter must be of type "String"')
    }
  }
  // vars
  if ( config.containsKey('vars') ){
    if ( config.vars instanceof ArrayList ){
      for (i=0; i<config.vars.size(); i++){
        optionsString = optionsString + "-var \"${config.vars[i]}\" "
      }
    }else{
      error('terraform - "vars" parameter must be of type "String[]"')
    }
  }
  // verifyPlugins
  if ( config.containsKey('verifyPlugins') ){
    if ( config.verifyPlugins instanceof Boolean ){
      optionsString = optionsString + "-verify-plugins=${config.verifyPlugins} "
    }else{
      error('terraform - "verifyPlugins" parameter must be of type "Boolean"')
    }
  }

  println "DOCKER IMAGE: ${config.dockerImage}"
  println "DOCKER IMAGE: ${config.dockerImage.getClass()}"
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
