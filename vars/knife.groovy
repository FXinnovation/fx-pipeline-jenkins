def call (Map config = [:]){
  optionsString = ''
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
      script: "${knifeCommand} knife ${subCommand} ${optionsString}"
    )
  }
}
