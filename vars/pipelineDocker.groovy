def call(Map config = [:], Map closures =[:]){
  if (!config.containsKey('publish')){
    config.publish = false
  }
  if (!config.containsKey('dockerBuild')){
    error('dockerBuild configuration is mandatory')
  }
  if (!config.containsKey('dockerPublish') && true == config.publish){
    error('dockerPublish configuration is mandatory when config.publish is set to true')
  }

  if (closures.containsKey('preBuild') && closures.preBuild instanceof Closure){
    stage('preBuild'){
      closures.preBuild()
    }
  }
  stage('build'){
    println scmInfo.branch
    println scmInfo.tag
    dockerImage.build(config.dockerBuild)
  }
  if (closures.containsKey('postBuild') && closures.postBuild instanceof Closure){
    stage('postBuild'){
      closures.postBuild()
    }
  }
  if (config.publish){
    if (closures.containsKey('prePublish') && closures.prePublish instanceof Closure){
      stage('prePublish'){
        closures.prePublish()
      }
    }
    stage('publish'){
      dockerImage.publish(config.dockerPublish)
    }
    if (closures.containsKey('postPublish') && closures.postPublish instanceof Closure){
      stage('postPublish'){
        closures.postBuild()
      }
    }
  }
}
