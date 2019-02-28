def call(Map config = [:]){
  if (!config.containsKey('dockerImage')) {
    config.dockerImage = 'fxinnovation/ansible-lint:latest'
  }
  if (!(config.dockerImage instanceof CharSequence)) {
    error('dockerImage parameter must be of type CharSequence')
  }
  if (!config.containsKey('options')) {
    config.options = '-p --parseable-severity'
  }
  if (!(config.options instanceof CharSequence)) {
    error('options parameter must be of type CharSequence')
  }
  if (!config.containsKey('commandTarget')) {
    config.commandTarget = '.'
  }
  if (!(config.commandTarget instanceof CharSequence)) {
    error('commandTarget parameter must be of type CharSequence')
  }
  if (!config.containsKey('rewritePath')) {
    config.rewritePath = true
  }
  if (!(config.rewritePath instanceof Boolean)) {
    error('rewritePath parameter must be of type Boolean')
  }


  def ansiblelintCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: 'ansible-lint'
  )

  execute(
    script: "${ansiblelintCommand} --version"
  )

  result = execute(
    script: "${ansiblelintCommand} ${config.options} ${config.commandTarget}",
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
