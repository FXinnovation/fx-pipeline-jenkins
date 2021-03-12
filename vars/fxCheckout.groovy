import com.fxinnovation.data.ScmInfo

def call(Map config = [:]){
  registerServices()
  checkout scm

  scmInfo = new ScmInfo(
    this.getCommitId(),
    this.getLastCommitId(),
    this.getBranch(),
    this.getDefaultBranch(),
    this.getTag(),
    this.getLatestTag(),
    this.getRepositoryName(scm)
  )

  printDebug(scmInfo.toString())

  return scmInfo
}

private String getCommitId() {
  return executeCommand('git rev-parse HEAD')
}

private String getLastCommitId() {
  return executeCommand('git rev-parse origin/'+this.getDefaultBranch())
}

private String getBranch() {
  branch = executeCommand('echo "${BRANCH_NAME}"')
  if ('' == branch) {
    branch = executeCommand('git rev-parse --abbrev-ref HEAD')
  }

  return branch
}

private String getTag() {
  return executeCommand('git describe --tags --exact-match')
}

private String getLatestTag() {
  return executeCommand('git describe --tags $(git rev-list --tags --max-count=1)')
}

private String getRepositoryName(scm) {
  if (scm.metaClass.respondsTo(scm, 'getUserRemoteConfigs')) {
    // Only works on specific implementation of the SCM class, forcing us to check if the method exists
    return scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
  } else {
    // Fallback command to attempt to get repository. Less robust than the scm command above, thus not default.
    def repository = executeCommand('basename -s .git `git config --get remote.origin.url`')

    if ('' == repository) {
      // Last attempt to get the repository name. Most unsure method, thus attempted last.
      repository = executeCommand('basename `git rev-parse --show-toplevel`')
    }

    return repository
  }
}

private String getDefaultBranch() {
  // This is not robust as “master” might not be the default branch
  // However Jenkins is unable to get HEAD pointer on remote, thus making it hard to get default branch
  return 'master'
}

private String executeCommand(String command) {
  try{
    return execute(script: command, hideStdout: true).stdout.trim()
  }catch(error){
    return ''
  }
}
