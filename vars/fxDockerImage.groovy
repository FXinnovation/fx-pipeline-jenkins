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
        if (config.pushLatest && scmInfo.isLastTag){
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
          ],
          [
            postBuild: {
              fxInspecDockerImage(
                image: config.image,
                tag: tags[0],
                namespace: config.namespace
              )
            }
          ]
        )
      }
    ]
  )
}
