import com.fxinnovation.factory.OptionStringFactory

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
    'dockerImage': '',
    'dockerAdditionalMounts': '',
    'dockerEnvironmentVariables': ''
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
    'dockerImage': '',
    'dockerAdditionalMounts': '',
    'dockerEnvironmentVariables': ''
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

  def optionStringFactory = new OptionStringFactory(this)
  optionStringFactory.createOptionString('=')

  if (config.containsKey('color')){
    optionStringFactory.addOption('-color', config.color, Boolean)
  }
  if (config.containsKey('debug') && config.debug){
    optionStringFactory.addOption('-debug')
  }
  if (config.containsKey('except')){
    optionStringFactory.addOption('-except', config.except)
  }
  if (config.containsKey('only')){
    optionStringFactory.addOption('-only', config.only)
  }
  if (config.containsKey('force') && config.force){
    optionStringFactory.addOption('-force')
  }
  if (config.containsKey('machineReadable') && config.machineReadable){
    optionStringFactory.addOption('-machine-readable')
  }
  if (config.containsKey('onError')){
    if (!config.onError != 'cleanup' || !config.onError != 'abort'){
      error('"onError" parameter must be either "cleanup" or "abort"')
    }
    optionStringFactory.addOption('-on-error', config.onError)
  }
  if (config.containsKey('parallel')){
    optionStringFactory.addOption('-parallel', config.parallel, Boolean)
  }
  if (config.containsKey('syntaxOnly') && config.syntaxOnly){
    optionStringFactory.addOption('-syntax-only')
  }
  if ( config.containsKey('varFile') ){
    optionStringFactory.addOption('-var-file', config.varFile)
  }
  if ( config.containsKey('vars') && config.vars){
    optionStringFactory.addOption('-var', config.vars, ArrayList)
  }

  packerCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: 'packer',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables
  )

  execute(
    script: "${packerCommand} --version"
  )

  return execute(
    script: "${packerCommand} ${config.subCommand} ${optionStringFactory.getOptionString().toString()} ${config.commandTarget}"
  )
}
