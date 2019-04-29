def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/chefdk:latest')
  mapAttributeCheck(config, 'options', CharSequence, '-D --force-default-config')
  mapAttributeCheck(config, 'commandTarget', CharSequence, './')
  

  def cookstyleCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand:  ''
  )

  execute(
    script: "${cookstyleCommand} cookstyle --version"
  )

  return execute(
    script: "${cookstyleCommand} cookstyle ${config.options} ${config.commandTarget}"

  )
}
