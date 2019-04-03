def call(Map config = [:]){
  checkout scm
  def scmInfo = [:]
  scmInfo.commitId = execute(
    script: 'git rev-parse HEAD'
  ).stdout.trim()
  scmInfo.branch = execute(
    script: 'echo "${BRANCH_NAME}"'
  ).stdout.trim()
  try{
    scmInfo.tag = execute(
    script: 'git describe --tags --exact-match'
    ).stdout.trim()
  }catch(error){
    scmInfo.tag = ''
  }
  try{
    latestTag = execute(
      script: 'git describe --tags $(git rev-list --tags --max-count=1)'
    ).stdout.trim()
    scmInfo.isLastTag = (latestTag == scmInfo.tag)
  }catch(error){
    scmInfo.isLastTag = false
  }
  scmInfo.isPullRequest = scmInfo.branch.matches('^PR-[0-9]*$')
  scmInfo.repositoryName = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]

  printDebug(scmInfo)

  return scmInfo
}
