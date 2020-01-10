import com.fxinnovation.data.ScmInfo
import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:], ScmInfo scmInfo){
  mapAttributeCheck(config, 'disablePublish', Boolean, false)
  mapAttributeCheck(config, 'dockerBuild', Map)
  mapAttributeCheck(config, 'dockerPublish', Map)

  closureHelper = new ClosureHelper(closures)

  closureHelper.executeWithinStage('preBuild')

  stage('build') {
    for (registry in config.dockerPublish.registries) {
      dockerImage.build(config.dockerBuild + [tags: this.getAllTags()] + [registry: registry])
    }
  }

  closureHelper.executeWithinStage('postBuild')

  if (!config.disablePublish) {
    println 'Skip publication because “disablePublish” = true.'
  }

  if (!scmInfo.isPublishableAsAnything()) {
    println 'Skip publication because this commit is not publishable as anything.'
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
    for (registry in config.dockerPublish.registries) {
      if (this.dockerTagExists(config.dockerPublish.namespace, registry, scmInfo.getPatchTag())) {
        println "Skip publication for “${scmInfo.getPatchTag()}” in “${registry}” because this version was already published."
        return
      }
      dockerImage.publish(config.dockerPublish + [tags: this.getAllTags()] + [registry: registry])
    }
  }
}

private void publishDev(Map config, ScmInfo scmInfo) {
  if (!scmInfo.isPublishableAsDev()) {
    println 'Skip *dev* publication because this commit is not publishable as a development version.'
    return
  }

  stage('publish dev') {
    config.dockerPublish.registries.each { registry ->
      dockerImage.publish(config.dockerPublish + [tags: [scmInfo.getTag()]] + [registry: registry])
    }
  }
}

private boolean dockerTagExists(CharSequence registry, CharSequence namespace, CharSequence tag) {
  return execute(
    script: "curl --silent -f -lSL ${[registry, namespace, 'tags', tag].removeAll(['']).join('/')} > /dev/null"
  )
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
