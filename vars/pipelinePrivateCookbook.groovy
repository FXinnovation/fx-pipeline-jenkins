def call(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('publish')){
    if (!config.containsKey('credentialId') && !(config.credentialId instanceof CharSequence)){
      error ('credentialId parameter is mandatory and must be of type CharSequence')
    }
    if (!config.containsKey('serverUrl') && !(config.serverUrl instanceof CharSequence)) {
      error ('serverUrl parameter is mandatory and must be of type CharSequence')
    }
    if (!config.containsKey('cookbookName') && !(config.cookbookName instanceof CharSequence)) {
      error ('cookbookName parameter is mandatory and must be of type CharSequence')
    }
    
    closures.publish = {
      cookbookUploadOutput = knife.cookbookUpload(
        credentialId: config.credentialId,
        serverUrl: config.serverUrl,
        commandTarget: config.cookbookName,
      ) 
      
      println cookbookUploadOutput.stderr
      if (cookbookUploadOutput.stderr =~ /ERROR: Could not find cookbook/) {
        error(cookbookUploadOutput.stderr)
      } 
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
