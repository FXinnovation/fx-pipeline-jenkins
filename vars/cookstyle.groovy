def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  if ( !config.containsKey('options') ){
    config.options = '-D --force-default-config'
  }

  def output = ''
  def dockerCommand = ''
  try{
    sh(
      returnStdout: true,
      script:       'docker --version'
    )
    dockerCommand = "docker run --rm -v \$(pwd):/data ${config.dockerImage}"
    println 'docker is available, going to launch cookstyle using docker.'
  }catch(dockerError){
    println 'docker is not available, assuming cookstyle is installed.'
  }
  sh '${dockerCommand} cookstyle --version'
  try{
    output = sh(
      returnStdout: true,
      script:       "${dockerCommand} cookstyle ${config.options} ./"
    ).trim()
    println output
  }catch(error){
    error(output)
  }
  return output
}
