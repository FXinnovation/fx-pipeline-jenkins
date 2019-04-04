def call(Map config = [:]){
  fxJob([
    pipeline: {
      Map scmInfo ->
      pipelineNodeJsYarn()
    }
  ])
}
