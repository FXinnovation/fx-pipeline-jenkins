def call(Map config = [:], Map closures = [:]){
  if (!closures.containsKey('publish')){
    closures.publish = {
      println 'Publishing to chef-server is not yet implemented'
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
