def getPullRequest(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('owner')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('repo')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('pullNumber')){
    error('credentialId parameter is mandatory.')
  }

  return gitea.get(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repo}/pulls/${config.pullNumber}",
    credentialId: config.credentialId
  )
}

def get(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('apiPath')){
    error('apiPath')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }

  encodedUrl = config.url + '/api/v1/' + config.apiPath
  withCredentials([
    usernamePassword(
      credentialsId: config.credentialId,
      passwordVariable: 'password',
      usernameVariable: 'username'
    )
  ]) {
    responseRaw = httpRequest(
      customHeaders: [
        [ maskValue: true, name: 'Authorization', value: 'token ' + password],
      ],
      timeout: 60,
      consoleLogResponseBody: false,
      quiet: true,
      url: encodedUrl
    )
  }
  response = readJSON text: responseRaw.content
  return response
}
