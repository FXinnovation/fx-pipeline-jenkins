def call (Map config = [:]){

  mapAttributeCheck(config, 'directory', CharSequence, '', '“directory” parameter is mandatory.')
  mapAttributeCheck(config, 'credentialsId', CharSequence, '', '“credentialsId” parameter is mandatory.')
  mapAttributeCheck(config, 'repoUrl', CharSequence, '', '“repoUrl” parameter is mandatory.')
  mapAttributeCheck(config, 'tag', CharSequence, '', '“tag” parameter is mandatory.')

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

    if ('' == tagExist.stdout) {
      error("There is no tag \"${config.tag}\" in the repo \"${config.repoUrl}\"")
    }

    execute (
      script: "git checkout ${config.tag}"
    )
  }
}
