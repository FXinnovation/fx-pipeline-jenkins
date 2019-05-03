import com.fxinnovation.utils.OptionString

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
      error("packer - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }

  packer(config)
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
      error("packer - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }

  packer(config)
}

def call(Map config = [:]){
  optionsString = ""
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/packer:latest')
  mapAttributeCheck(config, 'subCommand', CharSequence, '', '"subCommand" parameter is mandatory and must be of type CharSequence.')
  mapAttributeCheck(config, 'commandTarget', CharSequence, '', '"commandTarget" parameter is mandatory and must be of type CharSequence.')
  mapAttributeCheck(config, 'dockerAdditionalMounts', Map, [:])
  mapAttributeCheck(config, 'dockerEnvironmentVariables', Map, [:])

  def optionsString = new OptionString(this)
  optionsString.setDelimiter('=')

  if (config.containsKey('color')){
    optionsString.add('-color', config.color, Boolean)
  }
  if (config.containsKey('debug') && config.debug){
    optionsString.add('-debug')
  }
  if (config.containsKey('except')){
    optionsString.add('-except', config.except)
  }
  if (config.containsKey('only')){
    optionsString.add('-only', config.only)
  }
  if (config.containsKey('force') && config.force){
    optionsString.add('-force')
  }
  if (config.containsKey('machineReadable') && config.machineReadable){
    optionsString.add('-machine-readable')
  }
  if (config.containsKey('onError')){
    if (!config.onError != 'cleanup' || !config.onError != 'abort'){
      error('"onError" parameter must be either "cleanup" or "abort"')
    }
    optionsString.add('-on-error', config.onError)
  }
  if (config.containsKey('parallel')){
    optionsString.add('-parallel', config.parallel, Boolean)
  }
  if (config.containsKey('syntaxOnly') && config.syntaxOnly){
    optionsString.add('-syntax-only')
  }
  if ( config.containsKey('varFile') ){
    optionsString.add('-var-file', config.varFile)
  }
  if ( config.containsKey('vars') && config.vars){
    optionsString.add('-var', config.vars, ArrayList)
  }

  packerCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: 'packer',
    command: 'packer',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables
  )

  execute(
    script: "${packerCommand} --version"
  )

  return execute(
    script: "${packerCommand} ${config.subCommand} ${optionsString.toString()} ${config.commandTarget}"
  )
}
