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

  buildCausers = currentBuild.getBuildCauses()

  notifiedPeople = ""
  for (i=0; i < buildCausers.size(); i++){
    currentCause = buildCausers[i]
    if (currentCause.userName != null){
      notifiedPeople = notifiedPeople + "@" + currentCause.userName.replace(' ','.').toLowerCase() + " "
    }
  }
  if (notifiedPeople == ""){
    notifiedPeople = sh(
      returnStdout: true,
      script:       "git log -1 --pretty=format:'%an'"
    ).replace(' ','.').toLowerCase() + " "
  }

  message = """
  Build: *[${env.JOB_NAME}](${env.BUILD_URL}) #${env.BUILD_NUMBER}*
  Status: *${currentBuild.currentResult}*
  Notify: ${notifiedPeople}
  """

  if ( config.containsKey('message') ){
    message = message + "\nMessage: ${config.message}"
  }

  rocketSend(
    failOnError: config.failOnError,
    message:     message,
    rawMessage:  config.rawMessage,
    avatar:      config.avatar
  )
}
