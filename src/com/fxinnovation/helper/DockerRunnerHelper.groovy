package com.fxinnovation.helper

import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.io.Debugger

class DockerRunnerHelper {
  final DEFAULT_SHARE_PATH = '/data'

  private Script context
  private Debugger debugger
  private OptionStringFactory optionStringFactory
  private String dataBasepath

  private String runCommand

  DockerRunnerHelper(Script context, Debugger debugger, OptionStringFactory optionStringFactory, String dataBasepath) {
    this.context = context
    this.debugger = debugger
    this.optionStringFactory = optionStringFactory
    this.dataBasepath = dataBasepath
  }

  String prepareRunCommand(
    String image,
    String fallbackCommand,
    String command = '',
    Map additionalMounts = [:],
    Map environmentVariables = [:],
    String network = '',
    Boolean asDaemon = false,
    String name = '',
    String entrypoint = '',
    Boolean removeAfterRun = true
  ) {
    this.optionStringFactory.createOptionString(' ')

    if (!this.isDockerInstalled()) {
      println "Docker is not available, assuming the tool “${fallbackCommand}” is installed."
      return fallbackCommand
    }

    this.optionStringFactory.addOption('-w', this.DEFAULT_SHARE_PATH)
    this.optionStringFactory.addOption('-v', "${this.dataBasepath}:${this.DEFAULT_SHARE_PATH}")

    if (removeAfterRun) {
      this.optionStringFactory.addOption('--rm')
    }

    if (asDaemon) {
      this.optionStringFactory.addOption('-d')
    }

    if ('' != entrypoint) {
      this.optionStringFactory.addOption('--entrypoint', entrypoint)
    }

    if ('' != name) {
      this.optionStringFactory.addOption('--name', name)
    }

    additionalMounts.each{
      key, value -> this.optionStringFactory.addOption('-v', "${key}:\"${value}\"")
    }

    environmentVariables.each{
      key, value -> this.optionStringFactory.addOption('-e', "${key}=\"${value}\"")
    }

    if (['host', 'overlay', 'macvlan', 'none', 'bridge'].contains(network)) {
      this.optionStringFactory.addOption('--network', network)
    } else if ('' != network) {
      throw new Exception(network + ' is not a valid value for docker network.')
    }

    this.optionStringFactory.addOption(image)
    this.optionStringFactory.addOption(command)

    this.runCommand = "docker run -m 2GB ${this.optionStringFactory.getOptionString().toString()}"

    return this.runCommand
  }

  Map run(CharSequence image = '', throwError = true, Boolean forcePullImage = false) {
    if ('' ==  this.runCommand) {
      throw new Exception('Cannot run a docker command that was not prepared. Please call “prepareRunCommand”.')
    }

    if (forcePullImage && '' == image) {
      throw new Exception('Cannot pull empty image! If forcePullImage is set to true, you must pass a docker image name.')
    }

    if (this.debugger.debugVarExists()){
      this.context.execute(script: 'docker version')
    }

    if (forcePullImage) {
      this.context.execute(script: "docker pull ${image}")
    }

    def result = this.context.execute(
      throwError: throwError,
      script: this.runCommand
    )

    this.runCommand = ''

    return result
  }

  private Boolean isDockerInstalled() {
    try {
      this.context.execute(script: 'which docker', hideStdout: true)
      return true
    } catch(dockerVersionError) {
      return false
    }
  }
}
