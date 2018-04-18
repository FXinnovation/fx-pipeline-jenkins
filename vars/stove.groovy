def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  if ( !config.containsKey('options') ){
    config.options = '--no-git'
  }
  if ( !config.containsKey('tag') ){
    config.tag = ''
  }
  if ( !config.containsKey('credentialsId') ){
    error('stove.groovy - The credentialsId parameter must be set')
  }

  def dockerCommand = ''
  def keyPath       = ''
  def output        = ''
  def stoveKey
  def stovePassphrase
  def stoveUsername

  try{
    withCredentials([
      sshUserPrivateKey(
        credentialsId:      config.credentialsId,
        keyFileVariable:    stoveKey,
        passphraseVariable: stovePassphrase,
        usernameVariable:   stoveUsername
      )
    ]){
      try{
        sh "docker run --rm ${config.dockerImage} chef exec stove --version"
        dockerCommand = "docker run --rm -v \$(pwd):/data -v ${stoveKey}:/secrets/key.pem ${config.dockerImage} chef exec"
        keyPath = '/secrets/key.pem'
      }catch(err){
        dockerCommand = ''
        keyPath = stoveKey
      }
      if ( tag != '' ){
        sh "cat metadata.rb | grep -E '^version\\s' | grep '${config.tag}'"
      }
      output = command("${dockerCommand} stove ${config.options} --username ${stoveUsername} --key ${keyPath} ./").trim()
    }
  }catch(error){
    throw error
  }
  return output
}
