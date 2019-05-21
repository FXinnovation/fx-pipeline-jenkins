def call(Map config = [:]) {
  mapAttributeCheck(config, 'release', CharSequence, '', '')
  mapAttributeCheck(config, 'chart', CharSequence, '', '')
  mapAttributeCheck(config, 'version', CharSequence, '', '')
  mapAttributeCheck(config, 'repo', CharSequence, 'https://kubernetes-charts.storage.googleapis.com/')
  mapAttributeCheck(config, 'valuesFile', CharSequence, 'values.yaml')

  fxJob(
    [
      pipeline: { Map scmInfo ->
        if ( '' == scmInfo.tag || 'master' != scmInfo.branch ){
          deploy = false
        }else{
          deploy = true
        }
        pipelineHelmDeployment(
          helmConfig: [
            'values': config.valuesFile,
            'release': config.release,
            'chart': config.chart,
            'version': config.version,
            'repo': config.repo,
            install: true,
            wait: true
          ],
          deploy: deploy
        )
      }
    ]
)

}
