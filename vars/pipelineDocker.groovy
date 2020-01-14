import com.fxinnovation.data.ScmInfo
import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:], ScmInfo scmInfo){
  mapAttributeCheck(config, 'disablePublish', Boolean, false)
  mapAttributeCheck(config, 'dockerBuild', Map, [:], 'dockerBuild Map options are needed.')
  mapAttributeCheck(config, 'dockerPublish', Map, [:], 'dockerPublish Map options are needed.')

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preBuild')

  stage('build') {
    for (registry in config.dockerPublish.registries) {
      dockerImage.build(config.dockerBuild + [tags: this.getAllTags(scmInfo)] + [registry: registry])
    }
  }

  closureHelper.executeWithinStage('postBuild')

  if (config.disablePublish) {
    println 'Skip publication because “disablePublish” = true.'
    return
  }

  if (!scmInfo.isPublishableAsAnything()) {
    println 'Skip publication because this commit is not publishable as anything.'
    return
  }

  closureHelper.executeWithinStage('prePublish')

  this.publish(config, scmInfo)
  this.publishDev(config, scmInfo)

  closureHelper.executeWithinStage('postPublish')
}

private void publish(Map config, ScmInfo scmInfo) {
  if (!scmInfo.isPublishable()) {
    println 'Skip *latest tag* publication because this commit is not publishable as latest version.'
    return
  }

  stage('publish') {
    config.dockerPublish.registries.each { registry ->
      dockerImage.publish(config.dockerPublish + [tags: this.getAllTags(scmInfo)] + [registry: registry])
    }
  }
}

private void publishDev(Map config, ScmInfo scmInfo) {
  if (!scmInfo.isPublishableAsPreRelease()) {
    println 'Skip *pre-release* publication because this commit is not publishable as a development version.'
    return
  }

  stage('publish dev') {
//    config.dockerPublish.registries.each { registry ->
      dockerImage.publish(config.dockerPublish + [tags: [scmInfo.getTag()]] + [registry: ''])
//    }
  }
}

private getAllTags(ScmInfo scmInfo) {
  return [
    scmInfo.getBranchAsDockerTag(),
    scmInfo.getMajorTag(),
    scmInfo.getMinorTag(),
    scmInfo.getTag(),
    'latest',
  ]
}
