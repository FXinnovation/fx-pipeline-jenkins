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
        pipelineDocker(
          [
            disablePublish: config.launchLocally,
            dockerBuild: [
              image: config.image,
              namespace: config.namespace
            ],
            dockerPublish: [
              image: config.image,
              registry: '',
              namespace: config.namespace,
              credentialId: 'jenkins-fxinnovation-dockerhub'
            ]
          ],
          [
            postBuild: {
              fxInspecDockerImage(
                image: config.image,
                tag: scmInfo.getBranchAsDockerTag(),
                namespace: config.namespace
              )
            }
          ],
          scmInfo
        )
      }
    ],
    [],
    config
  )
}
