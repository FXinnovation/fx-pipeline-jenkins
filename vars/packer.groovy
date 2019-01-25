def build(Map config = [:]){
  config.subCommand = 'build'
  validParameters = [
    'color':'',
    'debug':'',
    'except':'',
    'only':'',
    'force':'',
    'machineReadable':'',
    'onError':'',
    'parallel':'',
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

  packer.command(config)
}

def validate(Map config = [:]){
  config.subCommand = 'validate'
  validParameters = [
    'except':'',
    'only':'',
    'vars':'',
    'syntax-only': '',
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

  packer.command(config)
}

def command(Map config = [:]){
  optionsString = ""
  if (!config.containsKey('dockerImage')){
    config.dockerImage = 'fxinnovation/packer:latest'
  }
  if (!config.containsKey('subCommand') || !config.subCommand instanceof String){
    error('"subCommand" parameter is mandatory and must be of type String.')
  }
  if (!config.containsKey('commandTarget') || !config.commandTarger instanceof String){
    error('"commandTarget" parameter is mandatory and must be of type String.')
  }
  if (config.containsKey('color')){
    if (!config.color instanceof Boolean){
      error('"color" parameter must be of type Boolean')
    }
    optionsString = optionsString + "-color=${config.color} "
  }
  if (config.containsKey('debug')){
    if (!config.debug instanceof Boolean){
      error('"debug" parameter must be of type Boolean')
    }
    if (config.debug){
      optionsString = optionsString + '-debug '
    }
  }
  if (config.containsKey('except')){
    if (!config.except instanceof String){
      error('"except" parameter must be of type String')
    }
    optionsString = optionsString + "-except=${config.except} "
  }
  if (config.containsKey('only')){
    if (!config.only instanceof String){
      error('"only" parameter must be of type String')
    }
    optionsString = optionsString + "-only=${config.only} "
  }
  if (config.containsKey('force')){
    if (!config.force instanceof Boolean){
      error('"force" parameter must be of type Boolean')
    }
    if (config.force){
      optionsString = optionsString + '-force '
    }
  }
  if (config.containsKey('machineReadable')){
    if (!config.machineReadable instanceof Boolean){
      error('"machineReadable" parameter must be of type Boolean')
    }
    if (config.machineReadable){
      optionsString = optionsString + '-machine-readable '
    }
  }
  if (config.containsKey('onError')){
    if (!config.onError != 'cleanup' || !config.onError != 'abort'){
      error('"onError" parameter must be either "cleanup" or "abort"')
    }
      optionsString = optionsString + "-on-error=${config.onError} "
  }
  if (config.containsKey('parallel')){
    if (!config.parallel instanceof Boolean){
      error('"parallel" parameter must be of type Boolean')
    }
    optionsString = optionsString + "-parallel=${config.parallel} "
  }
  if (config.containsKey('syntaxOnly')){
    if (!config.syntaxOnly instanceof Boolean){
      error('"syntaxOnly" parameter must be of type Boolean')
    }
    if (config.syntaxOnly){
      optionsString = optionsString + '-syntax-only '
    }
  }
  if ( config.containsKey('varFile') ){
    if ( config.varFile instanceof String ){
      optionsString = optionsString + "-var-file=${config.varFile} "
    }else{
      error('"varFile" parameter must be of type "String"')
    }
  }
  if ( config.containsKey('vars') ){
    if ( config.vars instanceof ArrayList ){
      for (i=0; i<config.vars.size(); i++){
        optionsString = optionsString + "-var \"${config.vars[i]}\" "
      }
    }else{
      error('"vars" parameter must be of type "String[]"')
    }
  }

  try {
    execute(
      script: 'docker version'
    )
    packerCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage}"
    execute(
      script: "docker pull ${config.dockerImage}"
    )
  }catch(error){
    println 'Docker is not available, assuming packer is installed'
    packerCommand = 'packer'
  }

  execute(
    script: "${config.packerCommand} version"
  )

  return execute(
    script: "${packerCommand} ${config.subCommand} ${optionsString} ${config.commandTarget}"
  )
}
