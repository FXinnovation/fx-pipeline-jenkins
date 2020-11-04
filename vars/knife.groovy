import com.fxinnovation.factory.OptionStringFactory

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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
      error("knife - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
  mapAttributeCheck(config, 'commandTarget', CharSequence, '', '“commandTarget” parameter is mandatory.')
  mapAttributeCheck(config, 'credentialId', CharSequence, '', '“credentialId” parameter is mandatory.')
  mapAttributeCheck(config, 'serverUrl', CharSequence, '', '“serverUrl” parameter is mandatory.')
  mapAttributeCheck(config, 'subCommand', CharSequence, '', '“subCommand” parameter is mandatory.')
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/chefdk:3.3.0')
  mapAttributeCheck(config, 'format', CharSequence, '')
  mapAttributeCheck(config, 'freeze', Boolean, false)
  mapAttributeCheck(config, 'cookbookPath', CharSequence, '')
  mapAttributeCheck(config, 'secret', CharSequence, '')

  def optionStringFactory = new OptionStringFactory(this)
  optionStringFactory.createOptionString(' ')

  if ('' != config.format){
    optionStringFactory.addOption('--format', config.format)
  }

  if (true == config.freeze){
    optionStringFactory.addOption('--freeze')
  }

  if ('' != config.cookbookPath){
    optionStringFactory.addOption('--cookbook-path', config.cookbookPath)
  }

  if ('' != config.secret){
    optionStringFactory.addOption('--secret', config.secret)
  }

  optionStringFactory.addOption('--server-url', config.serverUrl)

  // Adding options because of CI environment. In a CI environment it is impossbile to edit the file on the fly.
  optionStringFactory.addOption('--disable-editing')
  optionStringFactory.addOption('--yes')

  withCredentials([
    sshUserPrivateKey(
      credentialsId: config.credentialId,
      keyFileVariable: 'key',
      passphraseVariable: 'passphrase',
      usernameVariable: 'username'
    )
  ]) {
    optionStringFactory.addOption('--user', username)
    optionStringFactory.addOption('--key', '/secret/chef')

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
      script: "${knifeCommand} knife ${config.subCommand} ${optionStringFactory.getOptionString().toString()} ${config.commandTarget}"
    )
  }
}
