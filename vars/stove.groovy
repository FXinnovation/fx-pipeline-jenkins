def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  if ( !config.containsKey('options') ){
    config.options = '--no-git'
  }
  if ( !config.containsKey('credentialsId') ){
    error('stove.groovy - The credentialsId parameter must be set')
  }

  def dockerCommand   = ''
  def keyPath         = ''
  def output          = ''

  try{
    withCredentials([
      sshUserPrivateKey(
        credentialsId:      config.credentialsId,
        keyFileVariable:    'stoveKey',
        passphraseVariable: 'stovePassphrase',
        usernameVariable:   'stoveUsername'
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
      sh "${dockerCommand} stove ${config.options} --username ${stoveUsername} --key ${keyPath} ./"
    }
  }catch(error){
    throw error
  }
  return output
}
