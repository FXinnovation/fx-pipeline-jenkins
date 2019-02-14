def environmentFromFile(Map config = [:]){
  config.subCommand = 'environment from file'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
    }
  }
  knife(config)
}

def call (Map config = [:]){
  optionsString = ''
  // commandTarget
  if (!config.containsKey('commandTarget') && !(config.commandTarget instanceof CharSequence)){
    error('commandTarget parameter is mandatory and must be of type CharSequence')
  }
  // credentialId
  if (!config.containsKey('credentialId') && !(config.credentialId instanceof CharSequence)){
    error('user parameter is mandatory and must be of type CharSequence')
  }
  // dockerImage
  if (!config.containsKey('dockerImage') && !(config.dockerImage instanceof CharSequence)]){
    config.dockerImage = 'fxinnovation/chefdk'
  }
  // server-url
  if (config.containsKey('serverUrl') && config.serverUrl instanceof CharSequence){
    optionsString += "--server-url ${config.serverUrl} "
  }else{
    error('serverUrl parameter is mandatory and must be of type CharSequence')
  }
  // subCommand
  if (!config.containsKey('subCommand') && !(config.subCommand instanceof CharSequence)){
    error('subCommand parameter is mandatory and must be of type CharSequence')
  }
  // Adding options because of CI environment
  optionsString += '--disable-editing --yes '

  withCredentials([
    sshUserPrivateKey(
      credentialsId: config.credentialId,
      keyFileVariable: 'key',
      passphraseVariable: 'passphrase',
      usernameVariable: 'username'
    )
  ]) {
    // user
    optionsString += "--user ${username} "
    // key
    optionsString += "--key /secret/chef"

    knifeCommand = dockerRunCommand(
      dockerImage: config.dockerImage,
      fallbackCommand:  '',
      additionalMounts: [
        (key): '/secret/chef'
      ]
    )

    execute(
      script: "${knifeCommand} knife --version"
    )

    return execute(
      script: "${knifeCommand} knife ${subCommand} ${optionsString} ${commandTarget}"
    )
  }
}
