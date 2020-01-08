import com.fxinnovation.data.ScmInfo

def call(Map config = [:]){
  fxJob(
    [
      pipeline: { ScmInfo scmInfo ->
        pipelinePacker(
          [
            validateConfig: [
              config.validateConfig
            ],
            buildConfig: [
              config.buildConfig
            ],
            publish: scmInfo.isPublishable()
          ]
        )
      }
    ]
  )
}
