import com.fxinnovation.factory.OptionStringFactory

def build(Map config = [:]) {
  this.checkConfig(config)

  execute(script: "docker build ${this.buildDockerOptionString(config, config.tags)} ./")
}

def publish(Map config = [:]) {
  this.checkConfig(config)

  if (config.containsKey('credentialId')) {
    mapAttributeCheck(config, 'registry', CharSequence, '', 'Because config.credentialId is defined, config.registry must also be defined.')
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

  for (tag in config.tags) {
    execute(script: "docker push ${this.buildDockerOptionString(config, [tag],'')}")
  }
}

/**
 * Builds the options string for "docker build" and "docker push".
 * @param Map config
 * @param List tags
 * @param String optionName
 * @return String
 */
private String buildDockerOptionString(Map config, List tags, String optionName = '--tag') {
  def optionStringFactory = new OptionStringFactory(this)
  optionStringFactory.createOptionString(' ')

  if ([] == config.registries) {
    for (tag in tags) {
      optionStringFactory.addOption(optionName, this.buildDockerTagOption(config, tag))
    }
  }

  for (registry in config.registries) {
    for (tag in tags) {
      optionStringFactory.addOption(optionName, this.buildDockerTagOption(config, tag, registry))
    }
  }

  return optionStringFactory.getOptionString().toString()
}

private String buildDockerTagOption(Map config, String tag, String registry = '') {
  def tagOptions = [registry, config.namespace, "${config.image}:${tag} "]
  tagOptions.removeAll([''])

  return tagOptions.join('/')
}

private String checkConfig(Map config) {
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'registries', List, [])
  mapAttributeCheck(config, 'namespace', CharSequence, '')
}
