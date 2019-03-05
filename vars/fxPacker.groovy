def call(Map config = [:]){
  fxJob(
    [
      pipeline: { Map scmInfo ->
        if ( '' != scmInfo.tag ){
          publish = true
        }else{
          publish = false
        }
        publish = true
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
