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
    sh 'docker run --rm fxinnovation/chefdk cookstyle --version'
    dockerCommand = "docker run --rm -v \$(pwd):/data ${config.dockerImage}"
  }catch(error){}
  try{
    output = command("${dockerCommand} cookstyle ${config.options} ./").trim()
  }catch(error){
    error(output)
  }
  return output
}
