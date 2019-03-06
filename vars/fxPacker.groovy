def call(Map config = [:]){
  fxJob(
    [
      pipeline: { Map scmInfo ->
        publish = false

        if ( '' != scmInfo.tag ){
          publish = true
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
