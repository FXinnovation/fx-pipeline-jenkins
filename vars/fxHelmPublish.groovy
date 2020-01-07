import com.fxinnovation.data.ScmInfo

def call(Map config = [:]) {
  mapAttributeCheck(config, 'repo', CharSequence, 'http://chartmuseum-chartmuseum:8080')
  mapAttributeCheck(config, 'chartName', CharSequence, '', 'chartName parameter is mandatory')

  fxJob(
    [
      pipeline: { ScmInfo scmInfo ->
        pipelineHelmPublish(
          chartName: config.chartName,
          repo: config.repo,
          deploy: scmInfo.isPublishable()
        )
      }
    ]
  )
}
