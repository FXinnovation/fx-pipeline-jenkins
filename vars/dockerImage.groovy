import com.fxinnovation.factory.OptionStringFactory

def build(Map config = [:]) {
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')

  execute(script: "docker build ${this.buildDockerOptionString(config, config.tags)} ./")
}

def publish(Map config = [:]) {
  mapAttributeCheck(config, 'tags', List, [], 'This tags key must be defined')

  if (config.containsKey('credentialId')) {
    withCredentials([
      usernamePassword(
        credentialsId: config.credentialId,
        passwordVariable: 'password',
        usernameVariable: 'username'
      )
    ]) {
      execute(script: "echo \'${password}\' > password_file", printCommand: false)
      execute(script: "cat password_file | docker login --username \'${username}\' --password-stdin ${config.registry}")
      execute(script: "rm password_file", printCommand: false)
    }
  }

  for (tag in config.tags) {
    execute(script: "docker push ${this.buildDockerOptionString(config, [tag],'')}")
  }
}

/**
 * Builds the options string for "docker build" and "docker push".
 * @param Map config
 * @param List tag
 * @param String optionName
 * @return String
 */
private String buildDockerOptionString(Map config, List tags, String optionName = '--tag') {
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'registry', CharSequence, '')
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  def optionStringFactory = new OptionStringFactory(this)
  optionStringFactory.createOptionString(' ')

  for (tag in tags) {
    optionStringFactory.addOption(optionName, this.buildDockerTagOption(config, tag))
  }

  return optionStringFactory.getOptionString().toString()
}

private String buildDockerTagOption(Map config, String tag) {
  def tagOptions = [config.registry, config.namespace, "${config.image}:${tag} "]
  tagOptions.removeAll([''])

  return tagOptions.join('/')
}
