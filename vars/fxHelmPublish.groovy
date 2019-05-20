def call(Map config = [:]) {
  mapAttributeCheck(config, 'repo', CharSequence, 'http://chartmuseum-chartmuseum:8080')
  mapAttributeCheck(config, 'chartName', CharSequence, '', 'chartName parameter is mandatory')

  fxJob(
    [
      pipeline: { Map scmInfo ->
        if ( '' == scmInfo.tag || 'master' != scmInfo.branch ){
          deploy = false
        }else{
          deploy = true
        }
        pipelineHelmPublish(
          chartName: config.chartName,
          repo: config.repo,
          deploy: deploy
        )
      }
    ]
  )
}
