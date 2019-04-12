def virtualenv(Map config = [:]){
  validParameters = [
          'version':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("python - Parameter \"${parameter.key}\" is not valid for \"virtualenv\", please remove it!")
    }
  }
  mapAttributeCheck(config, 'version', CharSequence, '3')

  if (config.version == "3") {
    pip = "pip3"
    executable = "python3"
  } else {
    pip = "pip"
    executable = "python"
  }

  config.subCommand = "${pip} install virtualenv; ${executable} -m virtualenv virtualenv;"

  python(config)
}

def test(Map config = [:]){
  validParameters = [
          'version':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("python - Parameter \"${parameter.key}\" is not valid for \"test\", please remove it!")
    }
  }

  config.subCommand = ". virtualenv/bin/activate; python tests;"

  python(config)
}

def lint(Map config = [:]){
  validParameters = [
          'version':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("python - Parameter \"${parameter.key}\" is not valid for \"lint\", please remove it!")
    }
  }
  mapAttributeCheck(config, 'version', CharSequence, '3')

  if (config.version == "3") {
    executable = "python3"
  } else {
    executable = "python"
  }

  config.subCommand = ". virtualenv/bin/activate; make lint;"

  python(config)
}


def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = "python:" + config.version
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


  pythonCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand:  'false',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables,
  )

  return execute(
    script: "${pythonCommand} /bin/bash -c \"${config.subCommand}\""
  )
}
