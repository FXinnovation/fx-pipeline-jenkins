def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  if ( !config.containsKey('options') ){
    config.options = '-D --force-default-config'
  }

  def cookstyleCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand:  ''
  )

  execute(
    script: "${cookstyleCommand} cookstyle --version"
  )

  return execute(
    script: "${cookstyleCommand} cookstyle ${config.options} ./"
  )
}
