def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/pylint:latest'
  }
  if ( !config.containsKey('options') ){
    config.options = ''
  }
  if ( !config.containsKey('filePattern') ){
    error('pylint.groovy - filePattern parameter is mandatory')
  }
  
  def output = ''
  def dockerCommand = 'pylint'
  try{
    sh "docker run --rm ${config.dockerImage} --version"
    dockerCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage}"
  }catch(error){}
    output = command("${dockerCommand} ${config.options} ${config.filePattern}").trim()
    return output
}
