def call(Map config = [:]) {
  if (!config.containsKey('credentialId') && !(config.credentialId instanceof CharSequence)){
    error ('"credentialId" parameter must be of type CharSequence')
  }
  if (!config.containsKey('serverUrl') && !(config.serverUrl instanceof CharSequence)) {
    error ('"serverUrl" parameter must be of type CharSequence')
  }
  if (!config.containsKey('cookbookName') && !(config.cookbookName instanceof CharSequence)) {
    error ('"cookbookName" parameter must be of type CharSequence')
  }
  if (!config.containsKey('publish') && !(config.publish instanceof Boolean)){
    error ('"publish" parameter must be of type Boolean')    
  }

  if (!config.containsKey('credentialId')) {
    config.credentialId = 'chef-server-demo'
  }
  if(!config.containsKey('serverUrl')) {
    config.serverUrl = 'https://chef-server.dazzlingwrench.fxinnovation.com/organizations/fx'
  }
  if (!config.containsKey('publish')){
    config.publish = false
  }
  
  fxJob([
    postPrepare: {
      sh 'ssh-keygen -t rsa -f /tmp/id_rsa -P \'\''
    },
    pipeline: {
      pipelinePrivateCookbook ([
        credentialId: config.credentialId,
        serverUrl: config.serverUrl,
        cookbookName: config.cookbookName,
        publish: config.publish,
        cookbookPath: "/",
      ])
    }
  ])
}
