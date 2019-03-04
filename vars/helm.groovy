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
    'chart', '',
    'release', ''
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
  if (!config.containKey('subCommand')){
    error('subCommand parameter is mandatory.')
  }
  if (!config.containsKey('chart')){
    config.chart = ''
  }
  if (!config.containsKey('release')){
    config.release = ''
  }
  if (config.containsKey('values') && (config.values instanceof CharSequence)){
    optionsString += "--values ${config.values} "
  }
  if (config.containsKey('version') && (config.version instanceof CharSequence)){
    optionsString += "--version ${confing.version} "
  }
  if (config.containsKey('repo') && (config.repo instanceof CharSequence)){
    optionsString += "--repo ${confing.repo} "
  }
  if (config.containsKey('namespace') && (config.namespace instanceof CharSequence)){
    optionsString += "--namespace ${confing.namespace} "
  }
  if (config.containsKey('install') && (config.install instanceof Boolean) && config.install){
    optionsString += "--install "
  }
  if (config.containsKey('dryRun') && (config.dryRun instanceof Boolean) && config.dryRun){
    optionsString += "--dry-run "
  }
  if (config.containsKey('force') && (config.force instanceof Boolean) && config.force){
    optionsString += "--force "
  }
  if (config.containsKey('recreatePods') && (config.recreatePods instanceof Boolean) && config.recreatePods){
    optionsString += "--recreate-pods "
  }
  if (config.containsKey('wait') && (config.wait instanceof Boolean) && config.wait){
    optionsString += "--wait "
  }
  if (config.containsKey('username') && (config.username instanceof CharSequence)){
    optionsString += "--username ${config.username} "
  }
  if (config.containsKey('password') && (config.password instanceof CharSequence)){
    optionsString += "--password ${config.password} "
  }

  return execute(
    script: "helm ${config.subCommand} ${optionsString} ${config.release} ${config.chart}"
  )
}
