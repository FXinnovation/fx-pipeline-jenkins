def build(Map config = [:]){
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  optionsString = ''
  config.tags.each { tag ->
    if (config.containsKey('registries') && [] != config.registries){
      config.registries.each { registry ->
        if (this.isPublishable(registry, config.namespace,  config.image + tag)) {
          return
        }
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
    def optionsString = ''
    if (config.containsKey('registries') && [] != config.registries){
      config.registries.each { registry ->
        optionsString = "${registry}/"
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
        optionsString = "${config.namespace}/"
      }
      optionsString += "${config.image}:${tag} "
      execute(
        script: "docker push ${optionsString}"
      )
    }
  }
}

private boolean isPublishable(CharSequence registry, CharSequence namespace, CharSequence tag) {
  return(
    this.dockerTagIsMaster(tag) ||
    this.dockerTagExists(registry, namespace, tag)
  )
}

private boolean dockerTagIsMaster(CharSequence tag) {
  return 'master' === tag
}

private boolean dockerTagExists(CharSequence registry, CharSequence namespace, CharSequence tag) {
  return execute(
    script: "curl --silent -f -lSL ${registry}/${namespace}/tags/${tag} > /dev/null"
  )
}