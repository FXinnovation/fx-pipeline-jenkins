def call(Map config){
  def dockerImage  = config.dockerImage ?: 'fxinnovation/chefdk:latest'
  def options      = config.options ?: '--no-git'
  def tag          = config.tag ?: ''

  def dockerCommand = ''
  def keyPath       = ''
  def output        = ''

  try{
    withCredentials([
      sshUserPrivateKey(
        credentialId:       config.credentialId,
        keyFileVariable:    stoveKey,
        passphraseVariable: stovePassphrase,
        usernameVariable:   stoveUsername
      )
    ]){
      try{
        sh "docker run --rm ${dockerImage} chef exec stove --version"
        dockerCommand = "docker run --rm -v \$(pwd):/data -v ${stoveKey}:/secrets/key.pem ${dockerImage} chef exec"
        keyPath = '/secrets/key.pem'
      }catch(err){
        dockerCommand = ''
        keyPath = stoveKey
      }
      if tag != '' {
        sh "cat metadata.rb | grep -E '^version\\s' | grep '${tag}'"
      }
      output = command("${dockerCommand} stove ${options} --username ${stoveUsername} --key ${keyPath} ./").trim()
    }
  }catch(error){
    error(output)
  }
  return output
}
