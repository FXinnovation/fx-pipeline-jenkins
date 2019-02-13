def call(Map config = [:]){
  checkout scm
  def scmInfo = [:]
  scmInfo.commitId = execute(
    script: 'git rev-parse HEAD'
  ).stdout.trim()
  scmInfo.branch   = execute(
    script: 'echo "${BRANCH_NAME}"'
  ).stdout.trim()
  try{
    scmInfo.tag = execute(
    script: 'git describe --tags --exact-match'
  ).stdout.trim()
  }catch(error){
    scmInfo.tag = ''
  }
  scmInfo.isPullRequest = scmInfo.branch.matches('^PR-[0-9]*$')
  return scmInfo
}
