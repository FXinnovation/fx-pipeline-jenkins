def call(
  String credentialId,
  Boolean debug        = false,
  String dockerImage   = 'fxinnovation/chefdk:latest',
  String options       = '--no-git',
  String tag           = ''
){
  try{
    log(
      message: 'Loading credentials'
      output:  debug
    )
    withCredentials([
      sshUserPrivateKey(
        credentialId:       credentialId,
        keyFileVariable:    stoveKey,
        passphraseVariable: stovePassphrase,
        usernameVariable:   stoveUsername
      )
    ]){
      log(
        message: 'Checking if docker is available'
        output:  debug
      )
      // Defining if docker is available on the machine
      try{
        sh "docker run --rm ${dockerImage} chef exec stove --version"
        def dockerCommand = "docker run --rm -v \$(pwd):/data -v ${stoveKey}:/secrets/key.pem ${dockerImage} chef exec"
        def keyPath = '/secrets/key.pem'
        log(
          message: 'Launching stove using docker'
          output:  debug
        )
      }catch(error){
        def dockerCommand = ''
        def keyPath = stoveKey
        log(
          message: 'Launching stove using native stove (must be preinstalled)'
          output:  debug
        )
      }
      log(
        message: 'Verifying if tag that was passed matches metadata.rb tag'
        output:  debug
      )
      if tag != '' {
        sh "cat metadata.rb | grep -E '^version\\s' | grep '${tag}'"
      }
      log(
        message: 'Launching stove'
        output:  debug
      )
      // Launch stove
      def output = command("${dockerCommand} stove ${options} --username ${stoveUsername} --key ${keyPath} ./").trim()
    }
  }catch(error){
    log(
      message: 'Stove failed throwing the error'
      output:  debug
    )
    // Send command output as error
    throw output
  }
  // Return output
  log(
    message: 'Stove was succesfull, returning output'
    output:  debug
  )
  return output
}
