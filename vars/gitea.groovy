import groovy.json.JsonBuilder

def call(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('apiPath')){
    error('apiPath')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('httpMode')){
    error('httpMode parameter is mandatory.')
  }

  encodedUrl = config.url + '/api/v1/' + config.apiPath
  withCredentials([
    usernamePassword(
      credentialsId: config.credentialId,
      passwordVariable: 'password',
      usernameVariable: 'username'
    )
  ]) {
    request = [
      customHeaders: [
        [ maskValue: true, name: 'Authorization', value: 'token ' + password],
        [ maskValue: false, name: 'Content-Type', value: 'application/json'], 
      ],
      timeout: 60,
      httpMode: config.httpMode,
      acceptType: 'APPLICATION_JSON',
      consoleLogResponseBody: false,
      quiet: true,
      url: encodedUrl
    ]
    if (config.httpMode == 'POST' || config.httpMode == 'PATCH'){
      request.requestBody = config.data
    }
    responseRaw = httpRequest(request)
    response = readJSON text: responseRaw.content
    return response
  }
}

def getCurrentUser(Map config = [:]){
  config.apiPath = 'user'
  config.httpMode = 'GET'

  return call(config)
}

def getPullRequest(Map config = [:]){
  if (!config.containsKey('owner')){
    error('owner parameter is mandatory.')
  }
  if (!config.containsKey('repository')){
    error('repository parameter is mandatory.')
  }
  if (!config.containsKey('pullNumber')){
    error('pullNumber parameter is mandatory.')
  }

  config.apiPath = "repos/${config.owner}/${config.repository}/pulls/${config.pullNumber}"
  config.httpMode = 'GET'
  return call(config)
}

def postComment(Map config = [:]){
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

  config.data = new JsonBuilder([body: config.message]).toString()
  config.apiPath = "repos/${config.owner}/${config.repository}/issues/${config.issueId}/comments"
  config.httpMode = 'POST'

  return call(config)
}

def patchComment(Map config = [:]){
  if (!config.containsKey('owner')){
    error('owner parameter is mandatory.')
  }
  if (!config.containsKey('repository')){
    error('repository parameter is mandatory.')
  }
  if (!config.containsKey('message')){
    error('message parameter is mandatory.')
  }
  if (!config.containsKey('commentId')){
    error('commentId parameter is mandatory.')
  }

  config.data = new JsonBuilder([body: config.message]).toString()
  config.apiPath = "repos/${config.owner}/${config.repository}/issues/comments/${config.commentId}"
  config.httpMode = 'PATCH'

  return call(config)
}

def publishOnPullRequest(Map config = [:]){
  if (!config.containsKey('pullNumber')){
    error('pullNumber parameter is mandatory.')
  }
  if (!config.containsKey('message')){
    error('message parameter is mandatory.')
  }
  if (!config.containsKey('owner')){
    error('owner parameter is mandatory.')
  }
  if (!config.containsKey('repository')){
    error('repository parameter is mandatory.')
  }

  userId = getCurrentUser(
    url: config.url,
    credentialId: config.credentialId
  ).id

  comments = getIssueComments(
    url: config.url,
    credentialId: config.credentialId,
    issueId: config.pullNumber,
    owner: config.owner,
    repository: config.repository
  )

  commentId = null

  for ( comment in comments ){
    if ( comment.user.id == userId ) {
      commentId = comment.id
      break;
    }
  }

  if ( commentId == null ){
    postComment(
      url: config.url,
      credentialId: config.credentialId,
      issueId: config.pullNumber,
      owner: config.owner,
      repository: config.repository,
      message: config.message
    )
  }else{
    patchComment(
      url: config.url,
      credentialId: config.credentialId,
      commentId: commentId,
      owner: config.owner,
      repository: config.repository,
      message: config.message
    )
  }
}

def getIssueComments(Map config = [:]){
  if (!config.containsKey('issueId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('owner')){
    error('owner parameter is mandatory.')
  }
  if (!config.containsKey('repository')){
    error('repository parameter is mandatory.')
  }

  config.apiPath = "repos/${config.owner}/${config.repository}/issues/${config.issueId}/comments"
  config.httpMode = 'GET'

  return call(config)
}

def getRepositoriesByOrganization(Map config = [:]) {
  mapAttributeCheck(config, 'organizationId', Integer, '')

  config.apiPath = "/orgs/{$config.organizationId.toString()}/repos"
  config.httpMode = 'GET'

  return call(config)
}

def getOrganizations(Map config = [:]) {
  config.apiPath = 'user/orgs'
  config.httpMode = 'GET'

  return call(config)
}
