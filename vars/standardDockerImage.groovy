import com.fxinnovation.data.ScmInfo

def call(Map config = [:]) {
  registerServices()

  mapAttributeCheck(config, 'image', CharSequence, '', 'Docker pipeline was called but no image parameter was given.')
  mapAttributeCheck(config, 'pushLatest', Boolean, false)
  mapAttributeCheck(config, 'namespace', CharSequence, 'fxinnovation')

  standardJob(
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
          ]
        )
      }
    ],
    [],
    config
  )
}
