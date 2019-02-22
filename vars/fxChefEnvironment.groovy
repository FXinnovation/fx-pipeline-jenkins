def call(Map config = [:]){
  if (!config.containsKey('credentialId') || !(config.credentialId instanceof CharSequence)){
    config.credentialId = 'chef-server-demo'
  }
  if (!config.containsKey('serverUrl') || !(config.serverUrl instanceof CharSequence)){
    config.serverUrl = 'https://chef-server.dazzlingwrench.fxinnovation.com/organizations/fx'
  }
  if (!config.containsKey('environmentFile') || !(config.environmentFile instanceof CharSequence)){
    config.environmentFile = 'environment.json'
  }
  fxJob(
    [
      pipeline: { Map scmInfo ->
        if ( '' != scmInfo.tag ){
          publish = true
        }else{
          publish = false
        }
        pipelineChefEnvironment(
          [
            publish: publish,
            knifeConfig: [
              credentialId: config.credentialId,
              serverUrl: config.serverUrl,
              commandTarget: config.environmentFile
            ]
          ]
        )
      }
    ]
  )
}
