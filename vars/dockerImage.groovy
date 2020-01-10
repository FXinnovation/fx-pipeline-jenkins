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

  execute(script: "docker push ${this.buildDockerOptionString(config, '')}")
}

/**
 * Builds the options string for "docker build" and "docker push".
 * @param Map config
 * @param String optionName
 * @return String
 */
private String buildDockerOptionString(Map config, String optionName = '--tag') {
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'registry', CharSequence, '')
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  config.tags.each { tag ->
    def optionsStringFactory = new OptionStringFactory()
    optionStringFactory.addOption(optionName, this.buildDockerTagOption(config, tag))
  }

  return optionsStringFactory.toString()
}

private String buildDockerTagOption(Map config, String tag) {
  return [config.registry, config.namespace, "${config.image}:${tag} "].removeAll(['']).join('/')
}
