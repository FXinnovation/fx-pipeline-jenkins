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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"environmentFromFile\", please remove it!")
    }
  }
  return knife(config)
}

def cookbookUpload(Map config = [:]){
  config.subCommand = 'cookbook upload'
  
  if (!(config.containsKey('freeze'))){  
    config.freeze = true
  }

  if (!(config.containsKey('cookbookName'))){
    error('cookbookName parameter is mandatory')  
  }

  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
    'freeze': '',
    'cookbookName': '',
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
  cookbookName = ''
  // commandTarget
  if (!config.containsKey('commandTarget') && !(config.commandTarget instanceof CharSequence)){
    error('commandTarget parameter is mandatory and must be of type CharSequence')
  }
  // credentialId
  if (!config.containsKey('credentialId') && !(config.credentialId instanceof CharSequence)){
    error('"credentialId" parameter is mandatory and must be of type CharSequence')
  }
  // dockerImage
  if (!config.containsKey('dockerImage') && !(config.dockerImage instanceof CharSequence)]){
    config.dockerImage = 'fxinnovation/chefdk:latest'
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
  // freeze
  if (config.containsKey('freeze')){
    if (!(config.freeze instanceof Boolean)){
      error('freeze parameter must be of type Boolean')
    }
    if (config.freeze) {
      optionsString += '--freeze ' 
    }
  }
  //cookbook name 
  if (config.constainsKey('cookbookName')) {
    if (!(config.cookbookName instanceof CharSequence)){
      error('cookbookName parameter must be of type CharSequence')
    }
    if (config.cookbookName) {
      cookbookName = config.cookbookName
    }
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
      script: "${knifeCommand} knife ${subCommand} ${optionsString} ${commandTarget} ${cookbookName}"
    )
  }
}
