import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  mapAttributeCheck(config, 'deploy', Boolean, false)

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preTest')

  stage('test'){
    lintConfig = [
      commandTarget: config.chartName
    ]
    helm.lint(lintConfig)
  }

  closureHelper.executeWithinStage('postTest')
  closureHelper.executeWithinStage('prePublish')

  stage('publish'){
    if (true == config.deploy){
      return helm.publish([
        chartFolder: config.chartName,
        repository: config.repo
      ])
    }else{
      println 'Publish stage has been skipped'
    }
  }

  closureHelper.executeWithinStage('postPublish')
}
