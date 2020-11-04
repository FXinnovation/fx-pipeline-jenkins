import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  mapAttributeCheck(config, 'publish', Boolean, false)

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preInstall')

  stage('install') {
    yarn.install()
  }

  closureHelper.executeWithinStage('postInstall')
  closureHelper.executeWithinStage('preTest')

  stage('test') {
    yarn.test()
  }

  closureHelper.executeWithinStage('postTest')
  closureHelper.executeWithinStage('preAudit')

  stage('audit') {
    yarn.audit()
  }

  closureHelper.executeWithinStage('postAudit')
}
