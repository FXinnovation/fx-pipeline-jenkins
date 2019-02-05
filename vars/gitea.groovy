import groovy.json.JsonBuilder

def getCurrentUser(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }

  return get(
    url: config.url,
    credentialId: config.credentialId,
    apiPath: "user"
  )
}

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

def patch(Map config = [:]){
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
      httpMode: 'PATCH',
      requestBody: config.data,
      acceptType: 'APPLICATION_JSON',
      consoleLogResponseBody: false,
      quiet: true,
      url: encodedUrl
    )
  }
  return responseRaw
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

  def data = new JsonBuilder([body: config.message]).toString()

  return post(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/issues/${config.issueId}/comments",
    credentialId: config.credentialId,
    data:         data
  )
}

def patchComment(Map config = [:]){
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
  if (!config.containsKey('message')){
    error('message parameter is mandatory.')
  }
  if (!config.containsKey('commentId')){
    error('commentId parameter is mandatory.')
  }

  def data = new JsonBuilder([body: config.message]).toString()

  return patch(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/issues/comments/${config.commentId}",
    credentialId: config.credentialId,
    data:         data
  )
}

def publishOnPullRequest(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('pullNumber')){
    error('credentialId parameter is mandatory.')
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
    }
  }

  if ( commentId == null ){
    postComment(
      url: config.url,
      credentialId: config.credentialId,
      issueId: config.issueId,
      owner: config.owner,
      repository: config.repository,
      message: config.message
    )
  }else{
    println ("Going to patch comment of user ${userId} on comment ${commentId}")
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
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('issueId')){
    error('credentialId parameter is mandatory.')
  }
  if (!config.containsKey('owner')){
    error('owner parameter is mandatory.')
  }
  if (!config.containsKey('repository')){
    error('repository parameter is mandatory.')
  }

  return get(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/issues/${config.issueId}/comments",
    credentialId: config.credentialId
  )
}
