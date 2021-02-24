def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/ansible:latest')
  mapAttributeCheck(config, 'options', CharSequence, '-p --parseable-severity')
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')
  mapAttributeCheck(config, 'rewritePath', Boolean, true)

  def ansibleLintCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: 'ansible-lint',
    command: 'ansible-lint',
    dataIsCurrentDirectory: config.dockerDataIsCurrentDirectory,
    dataBasepath: config.dockerDataBasepath,
  )

  execute(
    script: "${ansibleLintCommand} --version"
  )

  result = execute(
    script: "${ansibleLintCommand} ${config.options} ${config.commandTarget}",
    throwError: false
  )
  if (0 != result.statusCode) {
    // ansible-lint return error on stdout, so swap it with stderr if empty
    if (!result.stderr) {
        if (config.rewritePath) {
            result.stderr = result.stdout.replaceFirst("/data/", "").replaceAll("\n/data/", "\n")
        } else {
            result.stderr = result.stdout
        }
    }
    error(result.stderr)
  }
}
