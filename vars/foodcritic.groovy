def call(Map config = [:]){
  if ( !config.containsKey('cookbookPaths') ){
    config.cookbookPaths = './'
  }
  if ( !config.containsKey('options') ){
    config.options = ''
  }
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }

  def foodcriticCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand:  '',
    dataIsCurrentDirectory: config.dockerDataIsCurrentDirectory,
    dataBasepath: config.dockerDataBasepath,
  )

  return execute(
    script: "${foodcriticCommand} foodcritic ${config.options} ${config.cookbookPaths}"
  )
}
