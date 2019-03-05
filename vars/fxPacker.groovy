def call(Map config = [:]){
  fxJob(
    [
      pipeline: { Map scmInfo ->
        if ( '' != scmInfo.tag ){
          publish = true
        }else{
          publish = false
        }
        pipelinePacker(
          [
            validateConfig: [
              config.validateConfig
            ],
            buildConfig: [
              config.buildConfig
            ],
            publish: publish
          ]
        )
      }
    ]
  )
}
