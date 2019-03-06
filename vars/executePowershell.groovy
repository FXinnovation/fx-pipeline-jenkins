def call(Map config = [:]){
  
  if (!config.containsKey('dockerImage')){
    config.dockerImage = 'fxinnovation/powershell:latest'
  }
  if (!config.containsKey('command') || !config.subCommand instanceof CharSequence){
    error('"subCommand" parameter is mandatory and must be of type CharSequence.')
  }
  if (config.containsKey('options') && !config.options instanceof CharSequence){
    error('"options" parameter must be of type CharSequence.')
  }
  if ( !config.containsKey('environmentVariables')  || !(config.environmentVariables instanceof Map) ){
    config.environmentVariables = []
  }
  
  if ( !config.containsKey('options') ){
    config.options = ''
  }
 
  powershellCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    environmentVariables: config.environmentVariables,
    fallbackCommand:  'pwsh'
  )

  execute(
    script: "${powershellCommand} --version"
  )

  return execute(
    script: "${powershellCommand} ${config.options} ${config.command}"
  )
}
