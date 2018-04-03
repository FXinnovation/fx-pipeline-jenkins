def call(
  Boolean debug      = false,
  String dockerImage = 'fxinnovation/chefdk:latest',
  String options     = '-D --force-default-config'
){
  log(
    message: 'Checking if docker is available',
    output:  debug
  )
  // Defining if docekr is available on the machine
  try{
    sh 'docker run --rm fxinnovation/chefdk cookstyle --version'
    def dockerCommand = "docker run --rm -v \$(pwd):/data ${dockerImage}"
    log(
      message: 'Launching cookstyle using docker',
      output:  debug
    )
  }catch(error){
    def dockerCommand = ''
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
    // Launch cookstyle
    def output = command("${dockerCommand} cookstyle ${options} ./").trim()
  }catch(error){
    log(
      message: 'Cookstyle failed throwing the error',
      output:  debug
    )
    // Send command output as error
    throw output
  }
  // Return output
  log(
    message: 'Cookstyle was succesfull, returning output',
    output:  debug
  )
  return output
}
