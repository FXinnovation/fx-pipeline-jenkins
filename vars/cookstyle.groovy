def call(hashMap = [:]){
  def config = [:]
  hashMap.resolveStrategy = Closure.DELEGATE_FIRST
  hashMap.delegate = config

  def debug = config.debug ?: false
  def dockerImage = config.dockerImage ?: 'fxinnovation/chefdk:latest'
  def options = config.options ?: '-D --force-default-config'

  def output = ''
  def dockerCommand = ''
  log(
    message: 'Checking if docker is available',
    output:  debug
  )
  try{
    sh 'docker run --rm fxinnovation/chefdk cookstyle --version'
    dockerCommand = "docker run --rm -v \$(pwd):/data ${dockerImage}"
    log(
      message: 'Launching cookstyle using docker',
      output:  debug
    )
  }catch(error){
    log(
      message: 'Launching cookstyle using native cookstyle (must be preinstalled)',
      output:  debug
    )
  }
  try{
    log(
      message: 'Launching cookstyle',
      output:  debug
    )
    output = command("${dockerCommand} cookstyle ${options} ./").trim()
  }catch(error){
    log(
      message: 'Cookstyle failed throwing the error',
      output:  debug
    )
    error(output)
  }
  log(
    message: 'Cookstyle was succesfull, returning output',
    output:  debug
  )
  return output
}
