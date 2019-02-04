def call(){
  checkout scm
  def scmInfo = {}
  scmInfo.commitId = command('git rev-parse HEAD').trim()
  scmInfo.branch   = command('echo "${BRANCH_NAME}"').trim()
  try{
    scmInfo.tag = command('git describe --tags --exact-match').trim()
  }catch(error){
    scmInfo.tag = ''
  }
  //scmInfo.isPullRequest = scmInfo.branch.matches('^PR-\d$')
  return scmInfo
}
