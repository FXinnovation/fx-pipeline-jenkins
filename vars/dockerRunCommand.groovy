def call(Map config = [:]) {
  mapAttributeCheck(config, 'dockerImage', CharSequence, '', 'dockerRunCommand - "dockerImage" parameter must exists and be a String (implements CharSequence).')
  mapAttributeCheck(config, 'fallbackCommand', CharSequence, '', 'dockerRunCommand - "fallbackCommand" parameter must exists and be a String (implements CharSequence).')
  mapAttributeCheck(config, 'additionalMounts', Map, [:])
  mapAttributeCheck(config, 'command', CharSequence, '')
  mapAttributeCheck(config, 'environmentVariables', Map, [:])
  mapAttributeCheck(config, 'forcePullImage', Boolean, false)

  if (!this.isDockerInstalled()) {
    println "Docker is not available, assuming the tool “${config.fallbackCommand}” is installed."
    return config.fallbackCommand
  }

  if (config.forcePullImage) {
    execute(script: "docker pull ${config.dockerImage}")
  }

  def additionalMounts = ''

  config.additionalMounts.each{
    key, value -> additionalMounts += "-v ${key}:\"${value}\" "
  }
  def environmentVariables = ''
  config.environmentVariables.each{
    key, value -> environmentVariables += "-e ${key}=\"${value}\" "
  }

  if (null != env.DEBUG){
    execute(
      script: 'docker version'
    )
  }

  return "docker run --rm -v \$(pwd):/data ${additionalMounts} ${environmentVariables} -w /data ${config.dockerImage} ${config.command}"
}

private Boolean isDockerInstalled() {
  try {
    execute(script: 'which docker', hideStdout: true)
    return true
  } catch(dockerVersionError) {
    return false
  }
}
