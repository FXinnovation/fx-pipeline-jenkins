def call(
  Boolean debug      = false,
  String dockerImage = 'fxinnovation/chefdk:latest',
  String options     = ''
){
  log(
    message: 'Checking if docker is available',
    output:  debug
  )
  // Defining if docekr is available on the machine
  try{
    sh 'docker run --rm fxinnovation/chefdk foodcritic --version'
    def dockerCommand = "docker run --rm -v \$(pwd):/data ${dockerImage}"
    log(
      message: 'Launching foodcritic using docker',
      output:  debug
    )
  }catch(error){
    def dockerCommand = ''
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
    // Launch foodcritic
    def output = command("${dockerCommand} foodcritic ${options} ./").trim()
  }catch(error){
    log(
      message: 'Foodcritic failed throwing the error',
      output:  debug
    )
    // Send command output as error
    throw output
  }
  // Return output
  log(
    message: 'Foodcritic was succesfull, returning output',
    output:  debug
  )
  return output
}
