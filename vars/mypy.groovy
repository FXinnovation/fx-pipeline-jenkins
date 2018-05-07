def call(Map config = [:]){
  File currentScript = new File(getClass().protectionDomain.codeSource.location.path)

  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/pythonlinters:latest'
  }
  if ( !config.containsKey('options') ){
    config.options = ''
  }
  if ( !config.containsKey('filePattern') ){
    error(currentScript.getName() + ' - filePattern parameter is mandatory')
  }
  
  def output = ''
  def dockerCommand = 'mypy'
  try{
    sh "docker run --rm ${config.dockerImage} --version"
    dockerCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage} mypy"
  }catch(error){}
    output = command("${dockerCommand} ${config.options} ${config.filePattern}").trim()
    return output
}
