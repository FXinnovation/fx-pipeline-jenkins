def upgrade(Map config = [:]){
  config.subCommand = 'upgrade'
  validParameters = [
    'chart': '',
    'dryRun': '',
    'force': '',
    'install': '',
    'namespace': '',
    'password': '',
    'recreatePods': '',
    'release': '',
    'repo': '',
    'subCommand': '',
    'username': '',
    'values': '',
    'version': '',
    'wait': ''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("helm - Parameter \"${parameter.key}\" is not valid for \"upgrade\", please remove it!")
    }
  }
  mandatoryParameters = [
    'chart': '',
    'release': ''
  ]
  for ( parameter in mandatoryParameters ) {
    if ( !config.containsKey(parameter.key)){
      error("helm - Parameter \"${parameter.key}\" is mandatory for \"upgrade\", please specify it!")
    }
  }

  helm(config)
}

def lint(Map config = [:]){
  config.subCommand = 'lint'
  if (!config.containsKey('commandTarget')){
    config.commandTarget = './'
  }
  validParameters = [
    'namespace': '',
    'values': '',
    'strict': '',
    'commandTarget': '',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("helm - Parameter \"${parameter.key}\" is not valid for \"lint\", please remove it!")
    }
  }

  helm(config)
}

//TODO Implement this
//def repoAdd(Map config = [:]){
//  config.subCommand = 'repo add'
//
//  helm(config)
//}

def list(Map config = [:]){
  config.subCommand = 'list'
  validParameters = [
    'all':'',
    'commandTarget':'',
    'deleted': '',
    'deleting': '',
    'deployed': '',
    'failed': '',
    'namespace': '',
    'output': '',
    'pending': '',
    'subCommand': '',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("helm - Parameter \"${parameter.key}\" is not valid for \"list\", please remove it!")
    }
  }

  helm(config)
}

def rollback(Map config = [:]){
  config.subCommand = 'rollback'
  validParameters = [
    'force':'',
    'recreatePods': '',
    'subCommand': '',
    'timeout': '',
    'wait': ''
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("helm - Parameter \"${parameter.key}\" is not valid for \"rollback\", please remove it!")
    }
  }
  mandatoryParameters = [
    'release': '',
    'revision': '',
  ]
  for ( parameter in mandatoryParameters ) {
    if ( !config.containsKey(parameter.key)){
      error("helm - Parameter \"${parameter.key}\" is mandatory for \"upgrade\", please specify it!")
    }
  }

  helm(config)
}

def call(Map config = [:]){
  optionsString = ''
  if (config.containsKey('all') && (config.all instanceof Boolean) && config.all){
    optionsString += '--all '
  }
  if (!config.containsKey('chart')){
    config.chart = ''
  }
  if (!config.containsKey('commandTarget')){
    config.commandTarget = ''
  }
  if (config.containsKey('date') && (config.date instanceof Boolean) && config.date){
    optionsString += '--date '
  }
  if (config.containsKey('deleted') && (config.deleted instanceof Boolean) && config.deleted){
    optionsString += '--deleted '
  }
  if (config.containsKey('deleting') && (config.deleting instanceof Boolean) && config.deleting){
    optionsString += '--deleting '
  }
  if (config.containsKey('deployed') && (config.deployed instanceof Boolean) && config.deployed){
    optionsString += '--deployed '
  }
  if (config.containsKey('failed') && (config.failed instanceof Boolean) && config.failed){
    optionsString += '--failed '
  }
  if (config.containsKey('dryRun') && (config.dryRun instanceof Boolean) && config.dryRun){
    optionsString += "--dry-run "
  }
  if (config.containsKey('force') && (config.force instanceof Boolean) && config.force){
    optionsString += "--force "
  }
  if (config.containsKey('install') && (config.install instanceof Boolean) && config.install){
    optionsString += "--install "
  }
  if (config.containsKey('max') && (config.max instanceof Integer)){
    optionsString += "--max ${config.max} "
  }
  if (config.containsKey('namespace') && (config.namespace instanceof CharSequence)){
    optionsString += "--namespace ${config.namespace} "
  }
  if (config.containsKey('output') && ('json' == config.output || 'yaml' == config.output)){
    optionsString += "--output ${config.output} "
  }
  if (config.containsKey('password') && (config.password instanceof CharSequence)){
    optionsString += "--password ${config.password} "
  }
  if (config.containsKey('pending') && (config.pending instanceof Boolean) && config.pending){
    optionsString += '--pending '
  }
  if (!config.containsKey('release')){
    config.release = ''
  }
  if (config.containsKey('recreatePods') && (config.recreatePods instanceof Boolean) && config.recreatePods){
    optionsString += "--recreate-pods "
  }
  if (config.containsKey('repo') && (config.repo instanceof CharSequence)){
    optionsString += "--repo ${config.repo} "
  }
  // TODO: Reserve is a reserved word in groovy, need to find a workaround
  //if (config.containsKey('reverse') && (config.reverse instanceof Boolean) && config.reverse){
  //  optionsString += '--reserve '
  //}
  if (config.containsKey('strict') && (config.strict instanceof Boolean) && config.strict){
    optionsString += "--strict "
  }
  if (!config.containsKey('subCommand')){
    error('subCommand parameter is mandatory.')
  }
  if (config.comtaimsKey('timeout') && (config.timeout instanceof Integer)){
    optionsString += "--timeout ${config.timeout} "
  }
  if (config.containsKey('username') && (config.username instanceof CharSequence)){
    optionsString += "--username ${config.username} "
  }
  if (config.containsKey('values') && (config.values instanceof CharSequence)){
    optionsString += "--values ${config.values} "
  }
  if (config.containsKey('version') && (config.version instanceof CharSequence)){
    optionsString += "--version ${config.version} "
  }
  if (config.containsKey('wait') && (config.wait instanceof Boolean) && config.wait){
    optionsString += "--wait "
  }

  // NOTE: We're not using docker because it's very hard to use helm in isolation
  // this might become a future enhancement
  execute(
    script: 'helm version'
  )

  return execute(
    script: "helm ${config.subCommand} ${optionsString} ${config.release} ${config.chart} ${config.commandTarget}"
  )
}
