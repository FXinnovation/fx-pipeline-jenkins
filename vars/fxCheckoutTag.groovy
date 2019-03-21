def call (Map config = [:]){
  if (!config.containsKey('directory') && !(config.directory instanceof CharSequence)) {
    error('directory parameter is mandatory and must be of type CharSequence')
  }
  
  if (!config.containsKey('credentialsId') && !(config.credentialsId instanceof CharSequence)) {
    error('credentialsId parameter is mandatory and must be of type CharSequence')
  }
  
  if (!config.containsKey('repoUrl') && !(config.repoUrl instanceof CharSequence)) {
    error('repoUrl parameter is mandatory and must be of type CharSequence')
  }
  
  if (!config.containsKey('tag') && !(config.tag instanceof CharSequence)) {
    error('tag parameter is mandatory and must be of type CharSequence')
  }
 
  dir(config.directory) {
    git(
      credentialsId: config.credentialsId,
      changelog: false,
      poll: false,
      url: config.repoUrl
    )

    def tagExist = execute (
      script: "git rev-parse -q --verify \"refs/tags/${config.tag}\"",
      throwError: false
    )

    if ("" == tagExist.stdout) {
      error("There is no tag \"${config.tag}\" in the repo \"${config.repoUrl}\"")
    }

    execute (
      script: "git checkout ${config.tag}"
    )
  } 
}
