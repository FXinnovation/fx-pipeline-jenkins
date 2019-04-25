def call(Map config = [:]) {
  mapAttributeCheck(config, 'dockerImage', CharSequence, '', 'dockerRunCommand - "dockerImage" parameter must exists and be a String (implements CharSequence).')
  mapAttributeCheck(config, 'fallbackCommand', CharSequence, '', 'dockerRunCommand - "fallbackCommand" parameter must exists and be a String (implements CharSequence).')

  mapAttributeCheck(config, 'additionalMounts', Map, [:])
  mapAttributeCheck(config, 'command', CharSequence, '')
  mapAttributeCheck(config, 'environmentVariables', Map, [:])

  try {
    execute(
      script: 'docker version'
    )
  } catch(dockerVersionError) {
    if ( '' == config.fallbackCommand ){
      println 'Docker is not available, assuming the tool is installed.'
    }else{
      println "Docker is not available, assuming ${config.fallbackCommand} is installed."
    }
    return config.fallbackCommand
  }

  execute(
    script: "docker pull ${config.dockerImage}"
  )

  def additionalMounts = ''

  config.additionalMounts.each{
    key, value -> additionalMounts += "-v ${key}:\"${value}\" "
  }
  def environmentVariables = ''
  config.environmentVariables.each{
    key, value -> environmentVariables += "-e ${key}=\"${value}\" "
  }

  return "docker run --rm -v \$(pwd):/data ${additionalMounts} ${environmentVariables} -w /data ${config.dockerImage} ${config.command}"
}
