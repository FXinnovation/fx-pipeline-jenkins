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
  if (config.containsKey('environmentVariables') && !config.environmentVariables instanceof Map){
    error('"environmentVariables" parameter must be of type Map.')
  }
  if (config.containsKey('additionalMounts') && !config.additionalMounts instanceof Map){
    error('"additionalMounts" parameter must be of type Map.')
  }
  
  if ( !config.containsKey('environmentVariables')  || !(config.environmentVariables instanceof Map)){
    config.environmentVariables = []
  }
  if ( !config.containsKey('additionalMounts') || !(config.additionalMounts instanceof Map)){
    config.additionalMounts = []
  }
  
  powershellCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    environmentVariables: config.environmentVariables,
    additionalMounts: config.additionalMounts,
    fallbackCommand:  'pwsh',
  )

  execute(
    script: "${powershellCommand} --version"
  )

  return execute(
    script: "${powershellCommand} ${config.script}"
  )
}
