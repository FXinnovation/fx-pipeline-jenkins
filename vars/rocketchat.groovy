def findUserByMail(Map config = [:]){
  if (!config.containsKey('mail')){
    error('mail parameter is mandatory.')
  }
  if (!config.containsKey('rocketChatUrl')){
    error('rocketChatUrl parameter is mandatory.')
  }
  if (!config.containsKey('rocketChatCredentialId')){
    error('rocketchatCredentialId parameter is mandatory.')
  }

  encodedUrl = config.rocketChatUrl + '/api/v1/users.list?query=' + java.net.URLEncoder.encode("{\"emails.address\":{\"\$regex\":\"(?i)${config.mail}\"}}", "UTF-8")
  withCredentials([
    usernamePassword(
      credentialsId: config.rocketChatCredentialId,
      passwordVariable: 'password',
      usernameVariable: 'username'
    )
  ]) {
    responseRaw = httpRequest(
      customHeaders: [
        [ maskValue: true, name: 'X-Auth-Token', value: password],
        [ maskValue: true, name: 'X-User-Id', value: username],
      ],
      timeout: 60,
      consoleLogResponseBody: false,
      quiet: true,
      url: encodedUrl
    )
  }
  response = readJSON text: responseRaw.content
  if (response.success != true || response.total > 1){
    error('An error occured on rocketchat while fetching the user')
  }
  return response.users[0]
}
