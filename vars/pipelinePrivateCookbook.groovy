def call(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('publish')){
    if (!config.containsKey('credentialId')){
      error ('credentialId parameter is mandatory.')
    }
    if (!config.containsKey('serverUrl')) {
      error ('serverUrl parameter is mandatory.')
    }
    if (!config.containsKey('cookbookName')) {
      error ('cookbookName parameter is mandatory.')
    }
    
    closures.publish = {
      cookbookUpload(
        credentialId: config.credentialId,
        serverUrl: config.serverUrl,
        commandTarget: config.cookbookName,
      ) 
    }
  }
  if (!config.containsKey('foodcritic')){
    config.foodcritic = [
      options: '-t \'~FC078\''
    ]
  }
  pipelineCookbook(
    config,
    closures
  )
}
