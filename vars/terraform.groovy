def validate(Map config = [:]){
  config.subCommand = 'validate'
  validParameters = [
    'checkVariables':'',
    'noColor':'',
    'vars':'',
    'varFile':'',
    'subCommand':'',
    'dockerImage':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
    }
  }
  terraform(config)
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
    'subCommand':'',
    'commandTarget':'',
    'dockerImage':''
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
    'target':'',
    'vars':'',
    'varFile':'',
    'subCommand': '',
    'commandTarget':'',
    'dockerImage': ''
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
    'state-out':'',
    'target':'',
    'vars':'',
    'varFile':'',
    'subCommand':'',
    'dockerImage':'',
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

def call(Map config = [:]){
  // dockerImage
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = "fxinnovation/terraform:latest"
  }
  // subCommand
  if ( !config.containsKey('subCommand') ){
    error('ERROR: The subcommand must be defined!')
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
      optionsString = optionsString + "-chef-variables=${config.checkVariables} "
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
      optionsString - optionsString + "-parallelism=${config.parallelism} "
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
    for (i=0; i<config.tartgets.size(); i++){
      optionsString = optionsString + "-target ${config.target[i]} "
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
        optionsString = optionsString + "-var '${config.vars[i]}'"
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

  try {
    sh(
      returnStdout: true,
      script:       "docker version"
    )
    terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage}"
    sh(
      returnStdout: true,
      script:       "docker pull ${config.dockerImage}"
    )
  } catch(dockerError) {
    println 'Docker is not available, assuming terraform is installed'
    terraformCommand = 'terraform'
  }

  terraformVersion = sh(
    returnStdout: true,
    script:       "${terraformCommand} version"
  ).trim()

  println "Terraform version is:\n${terraformVersion}"

  sh "${terraformCommand} ${config.subCommand} ${optionsString} ${config.commandTarget}"
}
