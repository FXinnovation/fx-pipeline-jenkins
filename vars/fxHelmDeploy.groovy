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
            'values':'values.yaml',
            'release': 'chartmuseum',
            'chart': 'chartmuseum',
            'version': '2.3.1',
            'repo': 'https://kubernetes-charts.storage.googleapis.com/',
            install: true,
            wait: true
          ],
          deploy: deploy
        )
      }
    ]
)

}
