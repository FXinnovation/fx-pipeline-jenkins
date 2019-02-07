def call(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('publish')){
    closures.publish = {
      println 'Publishing to chef-server is not yet implemented'
    }
  }
  pipelineCookbook(
    config,
    closures
  )
}
