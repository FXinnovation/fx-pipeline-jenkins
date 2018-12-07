def call(Map config = [:]){
  node {
    result="SUCCESS"
    color="GREEN"
    notify=false
    message="No special message"
    try {
      ansiColor('xterm') {
        stage('checkout') {
          checkout scm
          commit_id = sh(
            returnStdout: true, 
            script: "git rev-parse HEAD"
          ).trim()
          tag_id = sh(
            returnStdout: true, 
            script: "git describe --tags --exact-match || git rev-parse HEAD"
          ).trim()
        }
        // Pre-deploy stage
        stage('pre-deploy') {
          withCredentials([
            file(
              credentialsId: 'dazzlingwrench-google-service-account', 
              variable: 'account'
            )
            ]){
              sh 'cat $account > ./account.json'
            }
          terraform.init()
        }
        withCredentials([
          usernamePassword(
            credentialsId: 'dazzlingwrench-k8s_password',
            passwordVariable: 'secret',
            usernameVariable: 'user'
          ),
          usernamePassword(
            credentialsId: 'jenkins_fxinnovation_bitbucket',
            usernameVariable: 'TF_bitbucket_username',
            passwordVariable: 'TF_bitbucket_password'
          )
        ]){
          stage('validate'){
            terraform.validate()
          }
          stage('plan') {
            terraform.plan(
              out: 'plan.out'
            )
          }
        }
        // Deploy stage
        stage("deploy") {
          if(tag_id != commit_id){
            try {
              terraform.apply(
                commandTarget: 'plan.out'
              )
            }catch (error_backup) {
              // Printing statefiles for recuperation purposes
              archiveArtifacts(
                allowEmptyArchive: true,
                artifacts: 'terraform.tfstat*'
              )
              throw (error_backup)
            }
          }else{
            sh 'echo "This is not a tagged version, skipping apply deployment"'
          }
        }
      }
    }catch (error){
      result="FAILED"
      color="RED"
      notify=true
      message=error
      throw (error)
    }finally {
      stage("notify"){
        fx_notify(
          status: result  
        )
      }
    }
  }
}
