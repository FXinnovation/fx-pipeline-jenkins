def getPullRequest(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('owner')){
    error('owner parameter is mandatory.')
  }
  if (!config.containsKey('repository')){
    error('repository parameter is mandatory.')
  }
  if (!config.containsKey('pullNumber')){
    error('pullNumber parameter is mandatory.')
  }

  return get(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/pulls/${config.pullNumber}",
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

def post(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('apiPath')){
    error('apiPath')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('data')){
    error('data parameter is mandatory.')
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
        [ maskValue: false, name: 'Content-Type', value: 'application/json'], 
      ],
      timeout: 60,
      httpMode: 'POST',
      requestBody: config.data,
      acceptType: 'APPLICATION_JSON',
      consoleLogResponseBody: false,
      quiet: true,
      url: encodedUrl
    )
  }
  return responseRaw
}

def postComment(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('owner')){
    error('owner parameter is mandatory.')
  }
  if (!config.containsKey('repository')){
    error('repository parameter is mandatory.')
  }
  if (!config.containsKey('issueId')){
    error('issueId parameter is mandatory.')
  }
  if (!config.containsKey('message')){
    error('message parameter is mandatory.')
  }
  def jsonOutput = new groovy.json.JsonOutput()

  def data = jsonOutput.toJson([body: config.message])

  return post(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/issues/${config.issueId}/comments",
    credentialId: config.credentialId,
    data:         data
  )
}
