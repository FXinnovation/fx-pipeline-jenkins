def call(Map config = [:]){
  mapAttributeCheck(config, 'image',     CharSequence, '', 'The image key must be defined')
  mapAttributeCheck(config, 'tag',       CharSequence, '', 'This tags key must be defined')
  mapAttributeCheck(config, 'registry',  CharSequence, '')
  mapAttributeCheck(config, 'namespace', CharSequence, '')
  mapAttributeCheck(config, 'baseline', CharSequence, 'https://github.com/FXinnovation/fx-inspec-docker-baseline/archive/master.tar.gz')

  try{
    infiniteLoopScript = """
    while true
    do
      sleep 15
    done
    """
    inspecConfig = """
    {
      "reporter": {
        "cli": {
          "stdout": true
        },
        "junit": {
          "stdout": false,
          "file": "inspec-results.xml"
        }
      }
    }
    """
    writeFile(
      file: 'inspec-config.json',
      text: inspecConfig
    )
    writeFile(
      file: 'infiniteLoop.sh',
      text: infiniteLoopScript
    )
    def dockerImage = ''
    if ('' != config.registry){
      dockerImage += "${config.registry}/"
    }
    if ('' != config.namespace){
      dockerImage += "${config.namespace}/"
    }
    dockerImage += "${config.image}:${config.tag}"
    execute(
      script: "docker run -d \
        -v \$(pwd):/data \
        -w /data \
        --name inspec-test \
        --entrypoint sh \
        ${dockerImage} \
        infiniteLoop.sh"
    )
    try{
      inspec.exec(
        target: 'docker://inspec-test',
        jsonConfig: 'inspec-config.json',
        dockerAdditionalMounts: [
          '/var/run/docker.sock': '/var/run/docker.sock'
        ],
        commandTarget: config.baseline
      )
    }catch(inspecExecError){
      println 'Inspec tests have failed, but we\'re still being nice for now'
    }
  }catch(inspecError){
    throw (inspecError)
  }finally{
    execute(
      script: 'docker kill inspec-test && docker rm inspec-test',
      throwError: false
    )
    junit(
      allowEmptyResults: true,
      testResults: 'inspec-results.xml'
    )
  }
}
