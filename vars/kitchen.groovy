def test(Map config = [:]){
  config.subCommand = 'test'
  validParameters = [
    'concurrency': '',
    'destroy': '',
    'color': '',
    'commandTarget': '',
    'dockerImage': '',
    'dockerAdditionalMounts': '',
    'dockerEnvironmentVariables': '',
    'subCommand': ''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("kitchen - Parameter \"${parameter.key}\" is not valid for \"test\", please remove it!")
    }
  }
  kitchen(config)
}

def call(Map config = [:]){
  optionsString = ''
  if ( !config.containsKey('dockerAdditionalMounts') ){
    config.dockerAdditionalMounts = [:]
  }
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  if ( !config.containsKey('dockerEnvironmentVariables') ){
    config.dockerEnvironmentVariables = [:]
  }
  if (!config.containsKey('color')){
    config.color = true
  }
  if (!(config.color instanceof Boolean)){
    error('color parameter must be of type boolean')
  }
  if (config.color){
    optionsString += '--color '
  }else{
    optionsString += '--no-color '
  }
  // commandTarget
  if (!config.containsKey('commandTarget') || !(config.commandTarget instanceof CharSequence)){
    config.commandTarget = ''
  }
  // concurrency
  if (config.containsKey('concurrency')){
    if (!(config.concurrency instanceof Integer)){
      error('concurrency parameter must be of type Integer')
    }
    optionsString += "--concurrency=${config.concurrency} "
  }
  // destroy
  if (!config.containsKey('destroy')){
    config.destroy = 'always'
  }
  if (!('always' == config.destroy || 'never' == config.destroy || 'passing' == config.destroy)){
    error('destroy parameter must be one of the following values: always, never, passing')
  }
  optionsString += "--destroy=${config.destroy} "
  // subCommand
  if (!config.containsKey('subCommand') && !(config.subCommand instanceof CharSequence)){
    error('subCommand parameter must be specified and should be of type CharSequence')
  }

  kitchenCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: '',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables,
    dataIsCurrentDirectory: config.dockerDataIsCurrentDirectory,
    dataBasepath: config.dockerDataBasepath,
  )

  execute(
    script: "${kitchenCommand} kitchen version"
  )

  return execute(
    script: "${kitchenCommand} kitchen ${config.subCommand} ${optionsString} ${config.commandTarget}"
  )
}
