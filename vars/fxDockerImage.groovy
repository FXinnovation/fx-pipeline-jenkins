def call(Map config = [:]){
  if (!config.containsKey('image') && !(config.image instanceof CharSequence)){
    error('image parameter is mandatory and must be of type CharSequence')
  }
  if (!config.containsKey('pushLatest') || !(config.pushLatest instanceof Boolean)){
    pushLatest = false
  }
  if (!config.containsKey('namespace') && !(config.namespace instanceof CharSequence)){
    config.namespace = 'fxinnovation'
  }

  fxJob(
    [
      pipeline: { Map scmInfo ->
        tags = [scmInfo.branch.replace('/','_')]
        if ( 'master' == scmInfo.branch || '' != scmInfo.tag){
          publish = true
        }else{
          publish = false
        }
        if ( '' != scmInfo.tag ){
          tags.add(scmInfo.tag)
        }
        if (config.pushLatest && '' != scmInfo.tag){
          tags.add('latest')
        }
        pipelineDocker(
          [
            dockerBuild: [
              image: config.image,
              tags: tags,
              namespace: config.namespace
            ],
            dockerPublish: [
              image: config.image,
              tags: tags,
              registry: '',
              namespace: config.namespace,
              credentialId: 'jenkins-fxinnovation-dockerhub'
            ],
            publish: publish
          ]
        )
        withEnv([
          "INSPEC_DOCKER_NAME=inspec-test",
          "INSPEC_DOCKER_NAMESPACE=${config.namespace}",
          "INSPEC_DOCKER_IMAGE=${config.image}",
          "INSPEC_DOCKER_TAG=${tags[0]}"
        ]){
          try{
            infiniteLoopScript = """
            while true
            do
              sleep 15
            done
            """
            inspecConfig = [
              reporter: [
                cli: [
                  stdout: true
                ],
                junit: [
                  stdout: false,
                  file: 'inspec-results.xml'
                ]
              ]
            ]
            writeJSON(
              file: 'inspec-config.json',
              json: inspecConfig
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
                ${config.namespace}/${config.image}:${tags[0]} \
                infiniteLoop.sh"
            )
            inspec.exec(
              target: 'docker://inspec-test',
              jsonConfig: 'inspec-config.json',
              dockerAdditionalMounts: [
                '/var/run/docker.sock': '/var/run/docker.sock'
              ],
              commandTarget: 'https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/inspec-docker-baseline/archive/master.tar.gz'
            )
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
      }
    ]
  )
}
