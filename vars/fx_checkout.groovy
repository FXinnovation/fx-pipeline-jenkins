def call(){
  checkout scm
  def scmInfo.commitId = command('git rev-parse HEAD').trim()
  def scmInfo.branch   = command('echo "${BRANCH_NAME}"').trim()
  try{
    def scmInfo.tag = command('git describe --tags --exact-match').trim()
  }catch(error){
    def scmInfo.tag = ''
  }
  return scmInfo
}
