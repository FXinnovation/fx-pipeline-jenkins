def call(Map config = [:]){
  if (!config.containsKey('tag') || !config.containsKey('image') || !config.containsKey('namespace')){
    error('Parameters tag, image and namespace are mandatory.')
  }
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
    execute(
      script: "docker run -d \
        -v \$(pwd):/data \
        -w /data \
        --name inspec-test \
        --entrypoint sh \
        ${config.namespace}/${config.image}:${config.tag} \
        infiniteLoop.sh"
    )
    try{
      inspec.exec(
        target: 'docker://inspec-test',
        jsonConfig: 'inspec-config.json',
        dockerAdditionalMounts: [
          '/var/run/docker.sock': '/var/run/docker.sock'
        ],
        commandTarget: 'https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/inspec-docker-baseline/archive/master.tar.gz'
      )
    }catch(uselessError){
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
