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
    if ( currentBuild.result != "null" ){
      config.status = currentBuild.result
    }else{
      config.status = currentBuild.currentResult
    }
  }
  if ( !config.containsKey('notifiedPeople')){
    config.notifiedPeople = ""
  }
  if ( !config.containsKey('color') ){
    switch (config.status){
      case 'SUCCESS':
        config.color = '#00FF00'
        break
      case 'FAILURE':
        config.color = '#FF0000'
        break
      default:
        config.color = '#0000FF'
        break
    }
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
  ${config.notifiedPeople}
  """

  if ( config.containsKey('message') ){
    message = message + "\nMessage: ${config.message}"
  }

  rocketSend(
    failOnError: config.failOnError,
    message:     message,
    rawMessage:  config.rawMessage,
    avatar:      config.avatar,
    attachments: [[
      audioUrl: '',
      authorIcon: '',
      authorName: '',
      color: config.color,
      imageUrl: '',
      messageLink: '',
      text: config.status,
      thumbUrl: '',
      title: "${env.JOB_NAME} #${env.BUILD_NUMBER}",
      titleLink: env.BUILD_URL,
      videoUrl: ''
    ]]
  )

}
