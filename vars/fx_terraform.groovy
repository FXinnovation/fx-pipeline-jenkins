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
          sh 'docker pull fxinnovation/terraform'
          sh 'gcloud --version && docker --version'
          withCredentials([
            file(
              credentialsId: 'dazzlingwrench-google-service-account', 
              variable: 'account'
            )
            ]){
              sh 'cat $account > ./account.json'
            }
          sh 'gsutil cp gs://fxinnovation-platforms/dazzlingwrench/terraform.tfstate ./'
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
          stage("test") {
            message = 'plan: FAILED'
            sh "docker run --rm -v \$(pwd):/data fxinnovation/terraform init ./"
            sh(
              returnStdout: false,
              script: "docker run --rm \
                -v \$(pwd):/data \
                fxinnovation/terraform plan \
                -out ./plan_file \
                -var 'k8s_password=${secret}'"
            )
            message = 'plan: SUCCESS'
          }
        }
        // Deploy stage
        stage("deploy") {
          if(tag_id != commit_id){
            try {
              sh(
                returnStdout: false,
                script: "docker run --rm \
                  -v \$(pwd):/data \
                  fxinnovation/terraform apply \
                  -backup terraform.tfstate.\$(date +%s) \
                  ./plan_file"
              )
            }catch (error_apply){
              try {
                sh "gsutil cp terraform.tfstat* gs://fxinnovation-platforms/dazzlingwrench/"
              }catch (error_backup) {
                sh "cat terraform.tfstat*"
                throw (error_backup)
              }
              // Throw error
              throw (error_apply)
            }
            // Upload statefiles in bucket
            try {
              sh 'gsutil cp terraform.tfstat* gs://fxinnovation-platforms/dazzlingwrench/'
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
