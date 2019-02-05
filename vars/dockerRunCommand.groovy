def call(Map config = [:]) {
  if ( !config.containsKey('dockerImage') || !(config.script instanceof CharSequence) ){
    error('dockerRunCommand - "dockerImage" parameter must exists and be a String (implements CharSequence).')
  }

  if ( !config.containsKey('fallbackCommand') || !(config.script instanceof CharSequence) ){
    error('dockerRunCommand - "fallbackCommand" parameter must exists and be a String (implements CharSequence).')
  }

  if ( !config.containsKey('additionalMounts') || !(config.additionalMounts instanceof Map) ){
    config.additionalMounts = []
  }

  if ( !config.containsKey('environmentVariables')  || !(config.environmentVariables instanceof Map) ){
    config.environmentVariables = []
  }

  try {
    sh(
      returnStdout: true,
      script:       'docker version'
    )
  } catch(dockerError) {
    println "Docker is not available, assuming ${fallbackCommand} is installed."
    return fallbackCommand
  }

  sh(
    returnStdout: true,
    script:       "docker pull ${config.dockerImage}"
  )

  def additionalMounts = config.additionalMounts.each{ key, value -> "-v ${key}:${value} " }
  def environmentVariables = config.environmentVariables.each{ key, value -> "-e ${key}:${value} " }

  return "docker run --rm -v \$(pwd):/data ${additionalMounts} ${environmentVariables} -w /data ${config.dockerImage}"
}
