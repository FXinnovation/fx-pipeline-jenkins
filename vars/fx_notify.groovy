def call(Map config = [:]){
  if ( !config.containsKey('failOnError') ){
    config.failOnError = true
  }
  if ( !config.containsKey('rawMessage') ){
    config.rawMessage = true
  }
  if ( !config.containsKey('avatar') ){
    config.avatar = 'https://cdn.iconscout.com/icon/free/png-512/jenkins-4-555576.png'
  }

  message = """
  Build: *[${env.JOB_NAME}](${env.BUILD_URL}) #${env.BUILD_NUMBER}*
  Status: *${currentBuild.currentResult}*
  """

  if ( "${env.CHANGE_AUTHOR_EMAIL}" != "null" ){
    message = message + "\nNotify: @${env.CHANGE_AUTHOR_EMAIL}"
  }
  if ( config.containsKey('message') ){
    message = message + "\nMessage: `${config.message}`"
  }

  rocketSend(
    failOnError: config.failOnError,
    message:     message,
    rawMessage:  config.rawMessage,
    avatar:      config.avatar
  )
}
