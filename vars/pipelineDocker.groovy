import com.fxinnovation.data.ScmInfo
import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:], ScmInfo scmInfo){
  mapAttributeCheck(config, 'disablePublish', Boolean, false)
  mapAttributeCheck(config, 'dockerBuild', Map)
  mapAttributeCheck(config, 'dockerPublish', Map)

  closureHelper = new ClosureHelper(closures)

  if (closureHelper.isDefined('preBuild')) {
    stage('preBuild') {
      closures.preBuild()
    }
  }

  stage('build') {
    for (registry in config.dockerPublish.registries) {
      dockerImage.build(config.dockerBuild + [tags: [
        scmInfo.getBranchAsDockerTag(),
        scmInfo.getMajorTag(),
        scmInfo.getMinorTag(),
        scmInfo.getTag(),
        'latest',
      ]] + [registry: registry])
    }
  }

  if (closureHelper.isDefined('postBuild')) {
    stage('postBuild') {
      closures.postBuild()
    }
  }

  if (!config.disablePublish) {
    println 'Skip publication because “disablePublish” = true.'
  }

  if (!scmInfo.isPublishableAsAnything()) {
    println 'Skip publication because this commit is not publishable as anything.'
  }

  if (closureHelper.isDefined('prePublish')) {
    stage('prePublish') {
      closures.prePublish()
    }
  }

  if (!scmInfo.isPublishable()) {
    println 'Skip *latest tag* publication because this commit is not publishable as latest version.'
    return
  }

  stage('publish') {
    for (registry in config.dockerPublish.registries) {
      if (execute(
        script: "curl --silent -f -lSL ${[registry, namespace, 'tags', tag].removeAll(['']).join('/')} > /dev/null"
      )) {
        println "Skip publication for “${scmInfo.getPatchTag()}” in “${registry}” because this version was already published."
        return
      }
      dockerImage.publish(config.dockerPublish + [tags: [
        scmInfo.getBranchAsDockerTag(),
        scmInfo.getMajorTag(),
        scmInfo.getMinorTag(),
        scmInfo.getTag(),
        'latest',
      ]] + [registry: registry])
    }
  }

//  this.publish(config, scmInfo)
//  this.publishDev(config, scmInfo)

  if (!scmInfo.isPublishableAsDev()) {
    println 'Skip *dev* publication because this commit is not publishable as a development version.'
    return
  }

  stage('publish dev') {
    config.dockerPublish.registries.each { registry ->
      dockerImage.publish(config.dockerPublish + [tags: [scmInfo.getTag()]] + [registry: registry])
    }
  }

  if (closureHelper.isDefined('postPublish')) {
    stage('postPublish') {
      closures.postPublish()
    }
  }
}
