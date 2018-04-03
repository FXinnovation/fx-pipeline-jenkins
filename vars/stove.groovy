def call(hashMap){
  def config = [:]
  hashMap.resolveStrategy = Closure.DELEGATE_FIRST
  hashMap.delegate = config

  def credentialId = config.credentialId
  def debug        = config.debug ?: false
  def dockerImage  = config.dockerImage ?: 'fxinnovation/chefdk:latest'
  def options      = config.options ?: '--no-git'
  def tag          = config.tag ?: ''

  def dockerCommand = ''
  def keyPath       = ''
  def output        = ''

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
        dockerCommand = "docker run --rm -v \$(pwd):/data -v ${stoveKey}:/secrets/key.pem ${dockerImage} chef exec"
        keyPath = '/secrets/key.pem'
        log(
          message: 'Launching stove using docker'
          output:  debug
        )
      }catch(error){
        dockerCommand = ''
        keyPath = stoveKey
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
      output = command("${dockerCommand} stove ${options} --username ${stoveUsername} --key ${keyPath} ./").trim()
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
