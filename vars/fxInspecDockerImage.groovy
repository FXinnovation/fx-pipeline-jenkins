def call(Map config = [:]) {
  mapAttributeCheck(config, 'image', CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tag', CharSequence, '', 'This tags key must be defined')
  mapAttributeCheck(config, 'registry', CharSequence, '')
  mapAttributeCheck(config, 'namespace', CharSequence, '')

  try {
    def dockerImage = ''
    if ('' != config.registry) {
      dockerImage += "${config.registry}/"
    }
    if ('' != config.namespace) {
      dockerImage += "${config.namespace}/"
    }
    dockerImage += "${config.image}:${config.tag}"
    execute(
      script: dockerRunCommand(
        dockerImage: dockerImage,
        fallbackCommand: 'inspec',
        command: '-c \'while true; do sleep 15; done\'',
        asDaemon: true,
        name: 'inspec-test',
        entrypoint: 'sh',
        dataIsCurrentDirectory: config.dockerDataIsCurrentDirectory,
        dataBasepath: config.dockerDataBasepath,
      )
    )
    try {
      inspec.exec(
        target: 'docker://inspec-test',
        reporter: 'cli junit2:inspec-results.xml',
        dockerAdditionalMounts: [
          '/var/run/docker.sock': '/var/run/docker.sock',
        ],
        commandTarget: 'https://github.com/FXinnovation/fx-inspec-docker-baseline/archive/master.tar.gz'
      )
    } catch (inspecExecError) {
      print(inspecExecError)
      println 'Inspec tests have failed, but we\'re still being nice for now'
    }
  } catch (inspecError) {
    throw (inspecError)
  } finally {
    execute(
      script: 'docker kill inspec-test',
      throwError: false
    )
    junit(
      allowEmptyResults: true,
      testResults: 'inspec-results.xml'
    )
  }
}
