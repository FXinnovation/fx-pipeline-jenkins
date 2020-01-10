import com.fxinnovation.factory.OptionStringFactory

def build(Map config = [:]){
  execute(script: "docker build ${this.buildDockerOptionString(config)} ./")
}

def publish(Map config = [:]){
  if (config.containsKey('credentialId')){
    withCredentials([
      usernamePassword(
        credentialsId: config.credentialId,
        passwordVariable: 'password',
        usernameVariable: 'username'
      )
    ]) {
      execute(script: "docker login --username \'${username}\' --password \'${password}\' ${config.registry}")
    }
  }

  execute(script: "docker push ${this.buildDockerOptionString(config)}")
}

/**
 * Builds the options string for "docker build" and "docker push".
 * @param Map config
 * @return String
 */
private String buildDockerOptionString(Map config) {
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  config.tags.each { tag ->
    def optionsStringFactory = new OptionStringFactory()
    if (this.configContainsRegistries(config)) {
      config.registries.each { registry ->
        optionStringFactory.addOption('', this.buildDockerTagOption(config, registry, tag))
      }
    } else {
      optionStringFactory.addOption('', this.buildDockerTagOption(config, '', tag))
    }
  }

  return optionsStringFactory.toString()
}

private String buildDockerTagOption(Map config, String registry, String tag) {
  return [registry, config.namespace, "${config.image}:${tag} "].removeAll(['']).join('/')
}

private boolean configContainsRegistries(Map config) {
  return (
    config.containsKey('registries') &&
    [] != config.registries
  )
}
