import com.fxinnovation.io.Debugger

def call(Map config = [:]) {
  mapAttributeCheck(config, 'dockerImage', CharSequence, '', 'dockerRunCommand - "dockerImage" parameter must exists and be a String (implements CharSequence).')
  mapAttributeCheck(config, 'fallbackCommand', CharSequence, '', 'dockerRunCommand - "fallbackCommand" parameter must exists and be a String (implements CharSequence).')
  mapAttributeCheck(config, 'additionalMounts', Map, [:])
  mapAttributeCheck(config, 'command', CharSequence, '')
  mapAttributeCheck(config, 'network', CharSequence, 'bridge')
  mapAttributeCheck(config, 'environmentVariables', Map, [:])
  mapAttributeCheck(config, 'forcePullImage', Boolean, false)
  mapAttributeCheck(config, 'asDaemon', Boolean, false)
  mapAttributeCheck(config, 'dataBasepath', CharSequence, '$(pwd)')
  mapAttributeCheck(config, 'dataIsCurrentDirectory', Boolean, false)

  def debugger = new Debugger(this)

  if (config.dataIsCurrentDirectory) {
    config.dataBasepath = new File(getClass().protectionDomain.codeSource.location.path).parent
    debugger.printDebug("Set ${config.dataBasepath} as basepath for docker commands.")
  }

  def deamonParam = '--rm'
  if (config.asDaemon) {
    deamonParam = '-d'
  }

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

  if (debugger.debugVarExists()){
    execute(
      script: 'docker version'
    )
  }

  def network = ''

  switch(config.network) {
   case 'host':
     network = '--network host'
     break;
   case 'overlay':
     network = '--network overlay'
     break;
   case 'macvlan':
     network = '--network macvlan'
     break;
   case 'none':
     network = '--network none'
     break;
   case 'bridge':
     break;
   default:
     error(config.network + ' is not a valid value for docker network.')
     break;
  }

  return "docker run ${deamonParam} -v ${config.dataBasepath}:/data ${network} ${additionalMounts} ${environmentVariables} -w /data ${config.dockerImage} ${config.command}"
}

private Boolean isDockerInstalled() {
  try {
    execute(script: 'which docker', hideStdout: true)
    return true
  } catch(dockerVersionError) {
    return false
  }
}
