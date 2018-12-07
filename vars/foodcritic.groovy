def call(Map config  = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  if ( !config.containsKey('options') ){
    config.options = ''
  }

  def dockerCommand = ''
  def output = ''

  try{
    sh "docker run --rm ${config.dockerImage} foodcritic --version"
    dockerCommand = "docker run --rm -v \$(pwd):/data ${config.dockerImage}"
  }catch(error){
  }
  try{
    output = command("${dockerCommand} foodcritic ${config.options} ./").trim()
  }catch(error){
    error(output)
  }
  return output
}
