import com.fxinnovation.data.ScmInfo

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
      pipeline: { ScmInfo scmInfo ->
        tags = [scmInfo.getBranch().replace('/','_')]
        if ( scmInfo.isTagged() ){
          tags.add(scmInfo.getTag())
        }
        if (config.pushLatest && scmInfo.isPublishable()){
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
            publish: scmInfo.isPublishable() || scmInfo.isPublishableAsDev()
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
