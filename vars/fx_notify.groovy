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
  def email = ""

  try {
    for (i=0; i < buildCausers.size(); i++){
      def currentCause = buildCausers[i]
      if (currentCause.userId != null){
        email =  currentCause.userId
        foundCausers = true
      }
    }
    if (!foundCausers){
      email = sh(
        returnStdout: true,
        script:       "git log -1 --pretty=format:'%ae'"
      ).trim()
    }
    config.notifiedPeople = config.notifiedPeople + " @" + email
  } catch(ex) {
    println("Error finding people to notify")
    println(ex.getMessage());
    println(ex.toString());
  }

  def message = """
  ${config.notifiedPeople}
  """

  if ( config.containsKey('message') ){
    message = message + "\nMessage: ${config.message}"
  }

  try {
    office365ConnectorSend(
      color: config.color,
      message: message,
      status: config.status,
      webhookUrl: 'https://outlook.office.com/webhook/5dcddcdb-f3b6-4525-abeb-70923810e553@219647b6-1ea6-409d-b9cc-0893cb535884/JenkinsCI/d507bf9f26b247d29c1acd3bcbed58ad/28a8f8a0-8b85-4ec6-a8db-3ad985265e84'
    )
  } catch(ex) {
    println("Error sending to office 365 connector")
    println(ex.getMessage());
    println(ex.toString());
  }
}
