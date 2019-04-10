def build(Map config = [:]){
  mapAttributeCheck(config, 'image', CharSequence)
  mapAttributeCheck(config, 'tags', List)
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence)

  optionsString = ''
  config.tags.each { tag ->
    optionsString += '--tag '
    if (config.containsKey('registries') && [] != config.registries){
      config.registries.each { registry ->
        optionsString += "${registry}/${config.namespace}/${config.image}:${tag} "
      }
    }
    optionsString += "${config.namespace}/${config.image}:${tag} "
  }

  execute(
    script: "docker build ${optionsString} ./"
  )
}

def publish(Map config = [:]){
  mapAttributeCheck(config, 'image', CharSequence)
  mapAttributeCheck(config, 'tags', List)
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence)
  mapAttributeCheck(config, 'credentialId', CharSequence)

  withCredentials([
    usernamePassword(
      credentialsId: config.credentialId,
      passwordVariable: 'password',
      usernameVariable: 'username'
    )
  ]) {
    execute(
      script: "docker login --username \'${username}\' --password \'${password}\' ${config.registry}"
    )
  }
  config.tags.each { tag ->
    optionsString = ''
    if (config.containsKey('registry') && '' != config.registry){
      optionsString += "${config.registry}/"
    }
    optionsString += "${config.namespace}/${config.image}:${tag} "
    execute(
      script: "docker push ${optionsString}"
    )
  }
}
