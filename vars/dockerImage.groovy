import com.fxinnovation.factory.OptionStringFactory

def build(Map config = [:]){
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  optionsStringFactory = new OptionStringFactory()
  config.tags.each { tag ->
    if (this.configContainsRegistries(config)) {
      config.registries.each { registry ->
        if (this.isPublishable(registry, config.namespace,  config.image + tag)) {
          return
        }
        optionStringFactory.addOption('--tag', this.buildDockerTagOption(config, registry, tag))
      }
    } else {
      optionStringFactory.addOption('--tag', this.buildDockerTagOption(config, '', tag))
    }
  }

  execute(
    script: "docker build ${optionsStringFactory.toString()} ./"
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

private String buildDockerTagOption(Map config, String registry, String tag) {
  def tagOption = ''

  if ('' != registry) {
    tagOption = "${registry}/"
  }

  if ('' != config.namespace) {
    tagOption += "${config.namespace}/"
  }

  tagOption += "${config.image}:${tag} "

  return tagOption
}

private boolean configContainsRegistries(Map config) {
  return (
    config.containsKey('registries') &&
    [] != config.registries
  )
}

private boolean dockerTagExists(CharSequence registry, CharSequence namespace, CharSequence tag) {
  return execute(
    script: "curl --silent -f -lSL ${registry}/${namespace}/tags/${tag} > /dev/null"
  )
}