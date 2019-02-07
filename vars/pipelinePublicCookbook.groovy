def call(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('publish')){
    if (!config.containsKey('supermarketCredentialsId')){
      error ('supermarketCredentialsId parameter is mandatory.')
    }
    closures.publish = {
      stove(
        credentialsId: config.supermarketCredentialsId
      )
    }
  }
  pipelineCookbook(
    config,
    closures
  )
}
