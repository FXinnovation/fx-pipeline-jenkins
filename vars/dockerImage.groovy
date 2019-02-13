def build(Map config = [:]){
  if (!config.containsKey('image') && !(config.image instanceof CharSequence)){
    error('image parameter is mandatory and must be of type CharSequence')
  }
  if (!config.containsKey('tags') && !(config.tags instanceof List)){
    config.tags = [scmInfo.branch]
    if ('' != scmInfo.tag){
      config.tags.add(scmInfo.tag)
    }
  }
  if (config.containsKey('registry') && !(config.registry instanceof CharSequence)){
    error('registry parameter is mandatory and must be of type CharSequence')
  }
  if (!config.containsKey('namespace') && !(config.namespace instanceof CharSequence)){
    error('namespace parameter is mandatory and must be of type CharSequence')
  }

  optionsString = ''
  config.tags.each { tag ->
    optionsString += '--tag '
    if (config.containsKey('registry')){
      optionsString += "${config.registry}/"
    }
    optionsString += "${config.namespace}/${config.image}:${tag} "
  }

  execute(
    script: "docker build ${optionsString} ./"
  )
}

def publish(Map config = [:]){
  if (!config.containsKey('image') && !(config.image instanceof CharSequence)){
    error('image parameter is mandatory and must be of type CharSequence')
  }
  if (!config.containsKey('tags') && !(config.tags instanceof List)){
    config.tags = [scmInfo.branch]
    if ('' != scmInfo.tag){
      config.tags.add(scmInfo.tag)
    }
  }
  if (!config.containsKey('registry') && !(config.registry instanceof CharSequence)){
    error('registry parameter is mandatory and must be of type CharSequence')
  }
  if (!config.containsKey('namespace') && !(config.namespace instanceof CharSequence)){
    error('namespace parameter is mandatory and must be of type CharSequence')
  }
  if (!config.containsKey('credentialId') && !(config.credentialId instanceof CharSequence)){
    error('credentialId parameter is mandatory and must be of type CharSequence')
  }

  withCredentials([
    usernamePassword(
      credentialsId: config.credentialId,
      passwordVariable: 'password',
      usernameVariable: 'username'
    )
  ]) {
    execute(
      script: "docker login --username ${username} --password ${password} ${config.registry}"
    )
  }
  optionsString = ''
  config.tags.each { tag ->
    optionsString += '--tag '
    if (config.containsKey('registry')){
      optionsString += "${config.registry}/"
    }
    optionsString += "${config.namespace}/${config.image}:${tag} "
  }
  execute(
    script: "docker push ${optionsString}"
  )
}
