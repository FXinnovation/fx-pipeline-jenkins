import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  mapAttributeCheck(config, 'publish', Boolean, false)

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preTest')

  stage('test'){
    packer.validate(config.validateConfig)
  }

  closureHelper.executeWithinStage('postTest')
  closureHelper.executeWithinStage('prePublish')
 
  stage('publish'){
    if (config.publish){
      packer.build(config.buildConfig)
    }else{
      println "Publish step is skipped"
    }
  }

  closureHelper.executeWithinStage('postPublish')
}
