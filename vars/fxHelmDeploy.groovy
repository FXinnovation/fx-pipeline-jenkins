import com.fxinnovation.data.ScmInfo

def call(Map config = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'release', CharSequence, '', '')
  mapAttributeCheck(config, 'chart', CharSequence, '', '')
  mapAttributeCheck(config, 'version', CharSequence, '', '')
  mapAttributeCheck(config, 'repo', CharSequence, 'https://kubernetes-charts.storage.googleapis.com/')
  mapAttributeCheck(config, 'valuesFile', CharSequence, 'values.yaml')

  standardJob(
    [
      pipeline: { ScmInfo scmInfo ->
        pipelineHelmDeployment(
          helmConfig: [
            values: config.valuesFile,
            release: config.release,
            chart: config.chart,
            version: config.version,
            repo: config.repo,
            install: true,
            wait: true
          ],
          deploy: scmInfo.isPublishable()
        )
      }
    ]
)

}
