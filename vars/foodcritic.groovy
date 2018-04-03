def call(hashMap = [:]){
  def config = [:]
  hashMap.resolveStrategy = Closure.DELEGATE_FIRST
  hashMap.delegate = config

  def debug       = config.debug ?: false
  def dockerImage = config.dockerImage ?: 'fxinnovation/chefdk:latest'
  def options     = config.options ?: ''

  def dockerCommand = ''
  def output = ''

  try{
    log(
      message: 'Checking if docker is available',
      output:  debug
    )
    sh "docker run --rm ${dockerImage} foodcritic --version"
    dockerCommand = "docker run --rm -v \$(pwd):/data ${dockerImage}"
    log(
      message: 'Launching foodcritic using docker',
      output:  debug
    )
  }catch(error){
    log(
      message: 'Launching foodcritic using native foodcritic (must be preinstalled)',
      output:  debug
    )
  }
  try{
    log(
      message: 'Launching foodcritic',
      output:  debug
    )
    output = command("${dockerCommand} foodcritic ${options} ./").trim()
  }catch(error){
    log(
      message: 'Foodcritic failed throwing the error',
      output:  debug
    )
    error(output)
  }
  log(
    message: 'Foodcritic was succesfull, returning output',
    output:  debug
  )
  return output
}
