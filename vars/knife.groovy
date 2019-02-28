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
    if (!validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"environmentFromFile\", please remove it!")
    }
  }
  return knife(config)
}

def environmentList(Map config = [:]){
  config.subCommand = 'environment list'
  config.commandTarget = ''
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
    'format':'',
  ]
  for ( parameter in config ) {
    if (!validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"environmentFromFile\", please remove it!")
    }
  }
  return knife(config)
}

def environmentShow(Map config = [:]){
  config.subCommand = 'environment show'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
    'format':'',
  ]
  for ( parameter in config ) {
    if (!validParameters.containsKey(parameter.key)){
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

  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
    'freeze': '',
  ]
  for ( parameter in config ) {
    if (!validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
    }
  }
  
  return knife(config)
}

def call (Map config = [:]){
  optionsString = ''
  
  if (!config.containsKey('commandTarget') && !(config.commandTarget instanceof CharSequence)){
    error('commandTarget parameter is mandatory and must be of type CharSequence')
  }
  
  if (!config.containsKey('credentialId') && !(config.credentialId instanceof CharSequence)){
    error('"credentialId" parameter is mandatory and must be of type CharSequence')
  }
  
  if (!config.containsKey('dockerImage') && !(config.dockerImage instanceof CharSequence)){
    config.dockerImage = 'fxinnovation/chefdk:latest'
  }
  
  if (!config.containsKey('serverUrl') && !(config.serverUrl instanceof CharSequence)){
    error('serverUrl parameter is mandatory and must be of type CharSequence')
  }

  if (!config.containsKey('subCommand') && !(config.subCommand instanceof CharSequence)){
    error('subCommand parameter is mandatory and must be of type CharSequence')
  }
  
  if (config.containsKey('format')){
    if (!config.format instanceof CharSequence){
      error('format parameter must of type CharSequence')
    }
    optionsString += "--format ${config.format} "
  }
  if (config.containsKey('freeze')){
    if (!(config.freeze instanceof Boolean)){
      error('freeze parameter must be of type Boolean')
    }
    if (config.freeze) {
      optionsString += '--freeze ' 
    }
  }
  
  optionsString += "--server-url ${config.serverUrl} "

  // Adding options because of CI environment. In a CI environment it is impossbile to edit the file on the fly.
  optionsString += '--disable-editing --yes '

  withCredentials([
    sshUserPrivateKey(
      credentialsId: config.credentialId,
      keyFileVariable: 'key',
      passphraseVariable: 'passphrase',
      usernameVariable: 'username'
    )
  ]) {
    optionsString += "--user ${username} "
    
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
      script: "${knifeCommand} knife ${config.subCommand} ${optionsString} ${config.commandTarget}"
    )
  }
}
