def build(Map config = [:]){
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  optionsString = ''
  config.tags.each { tag ->
    if (config.containsKey('registries') && [] != config.registries){
      config.registries.each { registry ->
        optionsString += "--tag ${registry}/"
        if ('' != config.namespace) {
          optionsString += "${config.namespace}/"
        }
        optionsString += "${config.image}:${tag} "
      }
    }else{
      optionsString += '--tag '
      if ('' != config.namespace) {
        optionsString += "${config.namespace}/"
      }
      optionsString += "${config.image}:${tag} "
    }
  }

  execute(
    script: "docker build ${optionsString} ./"
  )
}

def publish(Map config = [:]){
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  if (config.containsKey('credentialId')){
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
  }
  config.tags.each { tag ->
    optionsString = ''
    if (config.containsKey('registries') && [] != config.registries){
      config.registries.each { registry ->
        optionsString += "${registry}/"
        if ('' != config.namespace) {
          optionsString += "${config.namespace}/"
        }
        optionsString += "${config.image}:${tag} "
        execute(
          script: "docker push ${optionsString}"
        )
      }
    }else{
      if ('' != config.namespace) {
        optionsString += "${config.namespace}/"
      }
      optionsString += "${config.image}:${tag} "
    }
    execute(
      script: "docker push ${optionsString}"
    )
  }
}
