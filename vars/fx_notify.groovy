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
  if ( !config.containsKey('status')){
    if ( currentBuild.result != null ){
      config.status = currentBuild.result
    }else{
      config.status = currentBuild.currentResult
    }
  }
  if ( !config.containsKey('notifiedPeople')){
    config.notifiedPeople = ""
  }

  buildCausers = currentBuild.getBuildCauses()
  foundCausers = false

  for (i=0; i < buildCausers.size(); i++){
    currentCause = buildCausers[i]
    if (currentCause.userName != null){
      config.notifiedPeople = config.notifiedPeople + " @" + currentCause.userName.replace(' ','.').toLowerCase()
      foundCausers = true
    }
  }
  if (!foundCausers){
    config.notifiedPeople = config.notifiedPeople + " @" + sh(
      returnStdout: true,
      script:       "git log -1 --pretty=format:'%an'"
    ).replace(' ','.').toLowerCase()
  }

  message = """
  Build: *[${env.JOB_NAME}](${env.BUILD_URL}) #${env.BUILD_NUMBER}*
  Status: *${config.status}*
  Notify: ${config.notifiedPeople}
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
