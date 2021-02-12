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
  return executeCommand('echo "${BRANCH_NAME}"')
}

private String getTag() {
  return executeCommand('git describe --tags --exact-match')
}

private String getLatestTag() {
  return executeCommand('git describe --tags $(git rev-list --tags --max-count=1)')
}

private String getRepositoryName(scm) {
  return executeCommand('basename `git rev-parse --show-toplevel`')
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
