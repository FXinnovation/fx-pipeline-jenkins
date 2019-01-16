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

  encodedUrl = config.rocketChatUrl + "/api/v1/users.list?query=%7B%22emails.address%22%3A%7B%22%24regex%22%3A%22(%3Fi)${java.net.URLEncoder.encode(config.mail)}%22%7D%7D"
  withCredentials([
    usernamePassword(
      credentialsId: config.rocketChatCredentialId,
      passwordVariable: 'password',
      usernameVariable: 'username'
    )
  ]) {
    response_raw = httpRequest(
      customHeaders: [
        [ maskValue: true, name: 'X-Auth-Token', value: password],
        [ maskValue: true, name: 'X-User-Id', value: username],
      ],
      timeout: 60,
      consoleLogResponseBody: true,
      url: encodedUrl
    )
  }
  response = readJSON text: response_raw.content
  if (response.success != true){
    error('An error occured on rocketchat while fetching the user')
  }
  if (response.total > 1){
    error('I got multiple users from the request. WTF!')
  }
  return response.users[0]
}
