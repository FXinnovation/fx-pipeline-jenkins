import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preTest')

  stage('test'){
    packer.validate(config.validateConfig)
  }

  closureHelper.executeWithinStage('postTest')
  closureHelper.executeWithinStage('prePublish')

  stage('publish'){
    packer.build(config.buildConfig)
  }

  closureHelper.executeWithinStage('postPublish')
}
