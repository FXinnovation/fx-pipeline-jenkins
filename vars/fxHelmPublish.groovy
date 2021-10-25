import com.fxinnovation.data.ScmInfo

def call(Map config = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'repo', CharSequence, 'http://chartmuseum-chartmuseum:8080')
  mapAttributeCheck(config, 'chartName', CharSequence, '', 'chartName parameter is mandatory')

    standardJob(
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
