def call(Map config = [:]){
  if (config.containsKey('dockerImage') && !config.dockerImage instanceof CharSequence) {
    error('"dockerImage" parameter must be of type CharSequence.')
  }
  if (!config.containsKey('dockerImage')){
    config.dockerImage = 'fxinnovation/powershell:latest'
  }
  if (!config.containsKey('script') || !config.script instanceof CharSequence){
    error('"script" parameter is mandatory and must be of type CharSequence.')
  }
  if (config.containsKey('dockerEnvironmentVariables') && !config.dockerEnvironmentVariables instanceof Map){
    error('"dockerEnvironmentVariables" parameter must be of type Map.')
  }
  if (config.containsKey('dockerAdditionalMounts') && !config.dockerAdditionalMounts instanceof Map){
    error('"dockerAdditionalMounts" parameter must be of type Map.')
  }

  if (!config.containsKey('dockerEnvironmentVariables')){
    config.dockerEnvironmentVariables = [:]
  }
  if (!config.containsKey('dockerAdditionalMounts')){
    config.dockerAdditionalMounts = [:]
  }

  powershellCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    environmentVariables: config.dockerEnvironmentVariables,
    additionalMounts: config.dockerAdditionalMounts,
    fallbackCommand:  'pwsh',
    dataIsCurrentDirectory: config.dockerDataIsCurrentDirectory,
    dataBasepath: config.dockerDataBasepath,
  )

  execute(
    script: "${powershellCommand} --version"
  )

  return execute(
    script: "${powershellCommand} ${config.script}"
  )
}
