def call(
  Boolean debug      = false,
  String dockerImage = 'fxinnovation/chefdk:latest',
  String options     = '--destroy=always -c 10'
){
  log(
    message: 'Checking if docker is available'
    output:  debug
  )
  // Defining if docekr is available on the machine
  try{
    sh 'docker run --rm fxinnovation/chefdk kitchen --version'
    def dockerCommand = "docker run --rm -v \$(pwd):/data ${dockerImage}"
    log(
      message: 'Launching kitchen using docker'
      output:  debug
    )
  }catch(error){
    def dockerCommand = ''
    log(
      message: 'Launching kitchen using native kitchen (must be preinstalled)'
      output:  debug
    )
  }
  try{
    log(
      message: 'Launching kitchen'
      output:  debug
    )
    // Launch kitchen
    def output = command("${dockerCommand} kitchen ${options}").trim()
  }catch(error){
    log(
      message: 'Kitchen failed throwing the error'
      output:  debug
    )
    // Send command output as error
    throw output
  }
  // Return output
  log(
    message: 'Kitchen was succesfull, returning output'
    output:  debug
  )
  return output
}
