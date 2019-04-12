def call(Map config = [:]) {
  if ( !config.containsKey('dockerImage') || !(config.dockerImage instanceof CharSequence) ){
    error('dockerRunCommand - "dockerImage" parameter must exists and be a String (implements CharSequence).')
  }

  if ( !config.containsKey('fallbackCommand') || !(config.fallbackCommand instanceof CharSequence) ){
    error('dockerRunCommand - "fallbackCommand" parameter must exists and be a String (implements CharSequence).')
  }

  if ( !config.containsKey('additionalMounts') || !(config.additionalMounts instanceof Map) ){
    config.additionalMounts = []
  }

  if ( !config.containsKey('environmentVariables')  || !(config.environmentVariables instanceof Map) ){
    config.environmentVariables = []
  }

//  try {
//    execute(
//      script: 'docker version'
//    )
//  } catch(dockerVersionError) {
//    if ( "" == config.fallbackCommand ){
//      println "Docker is not available, assuming the tool is installed."
//    }else{
//      println "Docker is not available, assuming ${config.fallbackCommand} is installed."
//    }
//    return config.fallbackCommand
//  }

  execute(
    script: "docker pull ${config.dockerImage}"
  )

  def additionalMounts = ""

  config.additionalMounts.each{
    key, value -> additionalMounts += "-v ${key}:\"${value}\" "
  }
  def environmentVariables = ""
  config.environmentVariables.each{
    key, value -> environmentVariables += "-e ${key}=\"${value}\" "
  }

  return "docker run --rm -v \$(pwd):/data ${additionalMounts} ${environmentVariables} -w /data ${config.dockerImage}"
}
