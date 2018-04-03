def call(hashMap = [:]){
  def config = [:]
  hashMap.resolveStrategy = Closure.DELEGATE_FIRST
  hashMap.delegate = config

  def debug         = config.debug ?: false
  def dockerImage   = config.dockerImage ?: 'fxinnovation/chefdk:latest'
  def options       = config.options ?: '--destroy=always -c 10'
  def dockerOptions = config.dockerOptions ?: '-v /tmp:/tmp'

  def dockerCommand = ''
  def output = ''
  log(
    message: 'Checking if docker is available',
    output:  debug
  )
  try{
    sh 'docker run --rm fxinnovation/chefdk kitchen --version'
    dockerCommand = "docker run --rm -v \$(pwd):/data ${dockerOptions} ${dockerImage}"
    log(
      message: 'Launching kitchen using docker',
      output:  debug
    )
  }catch(error){
    log(
      message: 'Launching kitchen using native kitchen (must be preinstalled)',
      output:  debug
    )
  }
  try{
    log(
      message: 'Launching kitchen',
      output:  debug
    )
    output = command("${dockerCommand} kitchen test ${options}").trim()
  }catch(error){
    log(
      message: 'Kitchen failed throwing the error',
      output:  debug
    )
    throw output
  }finally{
    sh "${dockerCommand} kitchen destroy -c 10"
  }
  log(
    message: 'Kitchen was succesfull, returning output',
    output:  debug
  )
  return output
}
