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
          image_tag = scmInfo.commitId
        }else{
          image_tag = scmInfo.tag
        }
      }
      stage('build'){
        sh "docker build \
               --build-arg \"VCS_REF\"=\"${scmInfo.commitId}\" \
               --build-arg \"VERSION\"=\"${image_tag}\" \
               --build-arg \"BUILD_DATE\"=\"\$(date -u +\"%Y-%m-%dT%H:%M:%SZ\")\" \
               -t ${config.imageName}:${image_tag} ."
        sh "docker inspect ${config.imageName}:${image_tag}"
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
        cleanWS()
      }
    }
  }
}
