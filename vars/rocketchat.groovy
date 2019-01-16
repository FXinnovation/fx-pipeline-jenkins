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

  withCredentials([
    usernamePassword(
      credentialsId: config.rocketChatCredentialId,
      passwordVariable: 'password',
      usernameVariable: 'username'
    )
  ]) {
    response_json = httpRequest(
      customHeaders: [
        [ maskValue: true, name: 'X-Auth-Token', value: password],
        [ maskValue: true, name: 'X-User-Id', value: username],
      ],
      timeout: 60,
      url: "${config.rocketChatUrl}/api/v1/users.list?query={\"emails.address\":{\"\$regex\":\"(?i)${config.mail}\"}}"
    )
  }
  response = readJSON text: response_json
  if (response.success != true){
    error('An error occured on rocketchat while fetching the user')
  }
  if (response.total != 1){
    error('I got multiple users from the request. WTF!')
  }
  return response.users[0]
}
