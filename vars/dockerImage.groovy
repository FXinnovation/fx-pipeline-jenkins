def build(Map config = [:]){
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '', 'The namespace key must be defined')

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
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '', 'The namespace key must be defined')
  mapAttributeCheck(config, 'credentialId', CharSequence, '', 'The credentialId key must be defined')

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
    if (config.containsKey('registries') && [] != config.registries){
      config.registries.each { registry ->
        optionsString += "${registry}/${config.namespace}/${config.image}:${tag} "
        execute(
          script: "docker push ${optionsString}"
        )
      }
    }else{
      optionsString += "${config.namespace}/${config.image}:${tag} "
    }
    execute(
      script: "docker push ${optionsString}"
    )
  }
}
