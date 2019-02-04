def call(Map config = [:]){
  if (config.containsKey('scm') && config.scm instanceof hudson.plugins.git.GitSCM){
    checkout config.scm
  }else{
    if (binding.hasVariable('scm') && scm instanceof hudson.plugins.git.GitSCM){
      checkout scm
    }else{
      error('advancedCheckout - scm parameter was not given and could not find the scm variable or config.scm or scm was not of class hudson.plugins.git.GitSCM')
    }
  }
  def scmInfo = {}
  scmInfo.commitId = command('git rev-parse HEAD').trim()
  scmInfo.branch   = command('echo "${BRANCH_NAME}"').trim()
  try{
    scmInfo.tag = command('git describe --tags --exact-match').trim()
  }catch(error){
    scmInfo.tag = ''
  }
  scmInfo.isPullRequest = scmInfo.branch.matches('^PR-\d$')
  return scmInfo
}
