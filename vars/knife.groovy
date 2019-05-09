def databagCreateBag(Map config = [:]) {
  config.subCommand = 'data bag create'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if (!validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"databagList\", please remove it!")
    }
  }
  return knife(config)
}

def databagList(Map config = [:]) {
  config.subCommand = 'data bag list'
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"databagList\", please remove it!")
    }
  }
  return knife(config)
}

def databagShow(Map config = [:]) {
  config.subCommand = 'data bag show'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
    'format':'',
    'secret':'',
  ]
  for ( parameter in config ) {
    if (!validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"databagShow\", please remove it!")
    }
  }
  return knife(config)
}

def databagFromFile(Map config = [:]) {
  config.subCommand = 'data bag from file'
  validParameters = [
    'dockerImage':'',
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
    'secret':'',
  ]
  for ( parameter in config ) {
    if (!validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"databagFromFile\", please remove it!")
    }
  }
  return knife(config)
}

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
    'cookbookPath': ''
  ]
  for ( parameter in config ) {
    if (!validParameters.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is not valid for \"validate\", please remove it!")
    }
  }
  
  madatoryParamaters = [
    'subCommand':'',
    'credentialId': '',
    'serverUrl': '',
    'commandTarget':'',
    'cookbookPath': ''
  ]
   
  for ( parameter in madatoryParamaters ) {
    if (!config.containsKey(parameter.key)){
      error("knife - Parameter \"${parameter.key}\" is mandatory for \"${config.subCommand}\" subCommand")
    }
  }

  return knife(config)
}

def call (Map config = [:]){
  optionsString = ''
  
  mapAttributeCheck(config, 'commandTarget', CharSequence, '', '“commandTarget” parameter is mandatory.')
  mapAttributeCheck(config, 'credentialId', CharSequence, '', '“credentialId” parameter is mandatory.')
  mapAttributeCheck(config, 'serverUrl', CharSequence, '', '“serverUrl” parameter is mandatory.')
  mapAttributeCheck(config, 'subCommand', CharSequence, '', '“subCommand” parameter is mandatory.')
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/chefdk:latest')
  mapAttributeCheck(config, 'format', CharSequence, '')
  mapAttributeCheck(config, 'freeze', Boolean, false)
  mapAttributeCheck(config, 'cookbookPath', CharSequence, '')
  mapAttributeCheck(config, 'secret', CharSequence, '')

  if ('' != config.format){
    optionsString += "--format ${config.format} "
  }

  if (true == config.freeze){
    optionsString += '--freeze '
  }
  
  if ('' != config.cookbookPath){
    optionsString += "--cookbook-path ${config.cookbookPath} "
  }
  
  if ('' != config.secret){
    optionsString += "--secret ${config.secret} "
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
