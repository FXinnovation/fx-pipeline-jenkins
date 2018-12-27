def call(Map config = [:]){
  if ( !config.containsKey('cookbookPaths') ){
    config.cookbookPaths = './'
  }
  if ( !config.containsKey('options') ){
    config.options = ''
  }
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }

  def foodcriticCommand = 'foodcritic'

  try{
    sh "docker --version"
    foodcriticCommand = "docker run --rm -v \$(pwd):/data ${config.dockerImage} foodcritic"
  }catch(error){}
  sh "${foodcriticCommand} ${config.options} ${config.cookbookPaths}"
}
