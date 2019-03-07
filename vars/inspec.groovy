def exec(Map config = [:]){
  config.subCommand = 'exec'
    validParameters = [
    'target': '',
    'jsonConfig': '',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("inspec - Parameter \"${parameter.key}\" is not valid for \"exec\", please remove it!")
    }
  }
  inspec(config)
}

def call(Map config = [:]){
  optionsString = ''
  if (!config.containsKey('commandTarget') || !(config.commandTarget instanceof CharSequence)){
    error('commandTarget parameter is mandatory')
  }
  if (!config.containsKey('dockerAdditionalMounts') || !(config.dockerAdditionalMounts instanceof CharSequence)){
    config.dockerAdditionnalMounts = [:]
  }
  if (!config.containsKey('dockerEnvironmentVariables') || !(config.dockerEnvironmentVariables instanceof CharSequence)){
    config.dockerEnvironmentVariables = [:]
  }
  if (!config.containsKey('dockerImage') || !(config.dockerImage instanceof CharSequence)){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  if (config.containsKey('target') && config.target instanceof CharSequence){
    optionsString += "--target=${config.target} "
  }
  if (config.containsKey('jsonConfig') && config.jsonConfig instanceof CharSequence){
    optionsString += "--json-config=${config.jsonConfig} "
  }
  // Please leave this option as latest one
  // if (config.containsKey('reporter') && config.reporter instanceof CharSequence){
  //   optionsString += "--reporter ${config.reporter} --"
  // }
  if (!config.containsKey('subCommand') || !(config.subCommand instanceof CharSequence)){
    error('subCommand parameter is mandatory')
  }

  inspecCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: '',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables
  )

  execute(
    script: "${inspecCommand} inspec --version"
  )

  return execute(
    script: "${inspecCommand} inspec ${config.subCommand} ${optionsString} ${config.commandTarget}"
  )
}
