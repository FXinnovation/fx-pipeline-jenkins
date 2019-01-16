def call (Map config = [:]){
  properties([
    buildDiscarder(
      logRotator(
        artifactDaysToKeepStr: '10',
        artifactNumToKeepStr: '10',
        daysToKeepStr: '10',
        numToKeepStr: '10'
      )
    ),
    pipelineTriggers([[
      $class: 'PeriodicFolderTrigger',
      interval: '1d']])
  ])
  node(){
    try{
      result = "SUCCESS"
      if (!config.containsKey('imageName')){
        error('The imageName parameter is mandatory.')
      }
      stage('pre-build'){
        scmInfo = fx_checkout()
        sh 'docker --version && docker images'
        if (scmInfo.tag == ''){
          imageTag = scmInfo.commitId
        }else{
          imageTag = scmInfo.tag
        }
      }
      stage('build'){
        sh "docker build \
               --build-arg \"VCS_REF\"=\"${scmInfo.commitId}\" \
               --build-arg \"VERSION\"=\"${imageTag}\" \
               --build-arg \"BUILD_DATE\"=\"\$(date -u +\"%Y-%m-%dT%H:%M:%SZ\")\" \
               -t ${config.imageName}:${imageTag} ."
        sh "docker inspect ${config.imageName}:${imageTag}"
      }
    }catch(error){
      result = "FAILURE"
    }finally{
      stage('notify'){
        fx_notify(
          status: result
        )
      }
      stage('clean'){
        cleanWs()
      }
    }
  }
}
