import com.fxinnovation.factory.OptionStringFactory
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
  mapAttributeCheck(config, 'name', CharSequence, '')
  mapAttributeCheck(config, 'entrypoint', CharSequence, '')
  mapAttributeCheck(config, 'dataBasepath', CharSequence, '$(pwd)')
  mapAttributeCheck(config, 'dataIsCurrentDirectory', Boolean, false)

  def optionStringFactory = new OptionStringFactory(this)
  optionStringFactory.createOptionString(' ')
  def debugger = new Debugger(this)

  if (!this.isDockerInstalled()) {
    println "Docker is not available, assuming the tool “${config.fallbackCommand}” is installed."
    return config.fallbackCommand
  }

  if (config.dataIsCurrentDirectory) {
    config.dataBasepath = new File(getClass().protectionDomain.codeSource.location.path).parent
    debugger.printDebug("Set ${config.dataBasepath} as basepath for docker commands.")
  }

  optionStringFactory.addOption('--rm')
  optionStringFactory.addOption('-w', '/data')
  optionStringFactory.addOption('-v', "${config.dataBasepath}:/data")

  if (config.asDaemon) {
    optionStringFactory.addOption('-d')
  }

  if (config.entrypoint != '') {
    optionStringFactory.addOption('--entrypoint', config.entrypoint)
  }

  if (config.name != '') {
    optionStringFactory.addOption('--name', config.name)
  }

  if (config.forcePullImage) {
    execute(script: "docker pull ${config.dockerImage}")
  }

  config.additionalMounts.each{
    key, value -> optionStringFactory.addOption('-v', "${key}:\"${value}\"")
  }

  config.environmentVariables.each{
    key, value -> optionStringFactory.addOption('-e', "${key}:\"${value}\"")
  }

  if (debugger.debugVarExists()){
    execute(
      script: 'docker version'
    )
  }

  if (['host', 'overlay', 'macvlan', 'none', 'bridge'].contains(config.network)) {
    optionStringFactory.addOption('--network', config.network)
  } else {
    error(config.network + ' is not a valid value for docker network.')
  }

  optionStringFactory.addOption(config.dockerImage)
  optionStringFactory.addOption(config.command)

  return "docker run ${optionStringFactory.getOptionString().toString()}"
}

private Boolean isDockerInstalled() {
  try {
    execute(script: 'which docker', hideStdout: true)
    return true
  } catch(dockerVersionError) {
    return false
  }
}
