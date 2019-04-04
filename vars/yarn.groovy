def audit(Map config = [:]){
  config.subCommand = 'audit'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("yarn - Parameter \"${parameter.key}\" is not valid for \"audit\", please remove it!")
    }
  }
}

def test(Map config = [:]){
  config.subCommand = 'test'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("yarn - Parameter \"${parameter.key}\" is not valid for \"test\", please remove it!")
    }
  }
}

def install(Map config = [:]){
  config.subCommand = 'install'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("yarn - Parameter \"${parameter.key}\" is not valid for \"install\", please remove it!")
    }
  }
}

def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/yarn:latest')
  mapAttributeCheck(config, 'dockerAdditionalMounts', Map, [:])
  mapAttributeCheck(config, 'dockerEnvironmentVariables', Map, [:])

  yarnCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: 'yarn'
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables
  )

  execute(
    script: "${yarnCommand} --version"
  )

  return execute(
    script: "${yarnCommand} ${config.subCommand}"
  )
}
