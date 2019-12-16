def call(Map config = [:]){
  if ( !config.containsKey('failOnError') ){
    config.failOnError = false
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

  def buildCausers = currentBuild.getBuildCauses()
  def foundCausers = false

  for (i=0; i < buildCausers.size(); i++){
    def currentCause = buildCausers[i]
    if (currentCause.userId != null){
      rocketUser = rocketchat.findUserByMail(
        mail: currentCause.userId,
        rocketChatUrl: 'https://gossip.dazzlingwrench.fxinnovation.com',
        rocketChatCredentialId: 'gossip.dazzlingwrench.fxinnovation.com-bot'
      )
      config.notifiedPeople = config.notifiedPeople + " @" + rocketUser.username
      foundCausers = true
    }
  }
  if (!foundCausers){
    def email = sh(
      returnStdout: true,
      script:       "git log -1 --pretty=format:'%ae'"
    ).trim()
    def rocketUser = [:]
    rocketUser = rocketchat.findUserByMail(
      mail: email,
      rocketChatUrl: 'https://gossip.dazzlingwrench.fxinnovation.com',
      rocketChatCredentialId: 'gossip.dazzlingwrench.fxinnovation.com-bot'
    )
    if (null == rocketUser || !rocketUser.containsKey('username')){
      rocketUser = [
        username: 'all'
      ]
    }
    config.notifiedPeople = config.notifiedPeople + " @" + rocketUser.username
  }

  def message = """
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

  office365ConnectorSend(
    color: config.color,
    message: message,
    status: config.status,
    webhookUrl: 'https://outlook.office.com/webhook/5dcddcdb-f3b6-4525-abeb-70923810e553@219647b6-1ea6-409d-b9cc-0893cb535884/JenkinsCI/d507bf9f26b247d29c1acd3bcbed58ad/28a8f8a0-8b85-4ec6-a8db-3ad985265e84'
  )
}
