import com.fxinnovation.data.ScmInfo
import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:], ScmInfo scmInfo){
  mapAttributeCheck(config, 'disablePublish', Boolean, false)
  mapAttributeCheck(config, 'dockerBuild', Map, [:], 'dockerBuild Map options are needed.')
  mapAttributeCheck(config, 'dockerPublish', Map, [:], 'dockerPublish Map options are needed.')
  // format: [registry: tokenForRegistry, registry2: tokenForRegistry2…]
  mapAttributeCheck(config, 'authTokens', Map, [:])

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preBuild')

  stage('build') {
    for (registry in config.dockerPublish.registries) {
      dockerImage.build(config.dockerBuild + [tags: this.getAllTags(scmInfo)] + [registry: registry])
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
    println(config.authTokens)
    config.dockerPublish.registries.each { account, registry ->
      println(account)
      println(registry)
      def authToken = config.authTokens.containsKey(account) ? config.authTokens[account] : ''
      if (this.dockerTagExists(registry, config.dockerPublish.namespace, scmInfo.getPatchTag(), authToken)) {
        println "Skip publication for “${scmInfo.getPatchTag()}” in “${registry}” because this version was already published."
        return
      }
      dockerImage.publish(config.dockerPublish + [tags: this.getAllTags(scmInfo)] + [registry: registry])
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

private boolean dockerTagExists(CharSequence registry, CharSequence namespace, CharSequence tag, CharSequence authToken = '') {
  def arguments = [registry, namespace, 'tags', tag]
  arguments.removeAll(['', null])

  def authHeader = ''
  if ('' != authToken) {
    authHeader = "-H \"Authorization: Basic ${authToken}\""
  }

  return execute(
    script: "curl --silent ${authHeader} -f -lSL https://${arguments.join('/')} > /dev/null"
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
