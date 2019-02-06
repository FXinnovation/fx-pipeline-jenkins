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
    response = httpRequest(request)
    println response
    return response
  }
}

def getCurrentUser(Map config = [:]){
  if (!config.containsKey('url')){
    error('url parameter is mandatory.')
  }
  if (!config.containsKey('credentialId')){
    error('credentialId parameter is mandatory.')
  }

  return call(
    url: config.url,
    credentialId: config.credentialId,
    apiPath: "user",
    httpMode: "GET"
  )
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

  return call(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/pulls/${config.pullNumber}",
    credentialId: config.credentialId,
    httpMode:     'GET'
  )
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

  def data = new JsonBuilder([body: config.message]).toString()

  return call(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/issues/${config.issueId}/comments",
    credentialId: config.credentialId,
    httpMode:     'POST',
    data:         data
  )
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

  def data = new JsonBuilder([body: config.message]).toString()

  return call(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/issues/comments/${config.commentId}",
    credentialId: config.credentialId,
    httpMode:     'PATCH',
    data:         data
  )
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

  println getCurrentUset(
    url: config.url,
    credentialId: config.credentialId
  )

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
      issueId: config.issueId,
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

  return call(
    url:          config.url,
    apiPath:      "repos/${config.owner}/${config.repository}/issues/${config.issueId}/comments",
    credentialId: config.credentialId,
    httpMode:     'GET'
  )
}
