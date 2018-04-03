def call(scm) {
  // Setting Job properties
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

  // Setting some variables
  notify      = false
  color       = "GREEN"
  result      = "SUCCESS"
  message     = "Build finished"
  
  // Call a slave 
  node() {
    // Encapsulate everythig into a try catch for error handling
    try {
      // Enable colored output in Jenkins
      ansiColor('xterm') {
        stage('pre-build'){
          scmInfo = fx_checkout scm
          // Generating new temporary key
          sh 'ssh-keygen -t rsa -f /tmp/id_rsa -P \'\''
        }

        // Foodcritic stage
        stage ('foodcritic'){
          output = foodcritic
        }

        // Cookstyle stage
        stage ('cookstyle'){
          output = cookstyle
        }

        // Kitchen stage
        stage ('kitchen') {
          output = kitchen
        }

        // Stove Stage
        stage ('publish') {
          if (scmInfo.tag != ''){
            output = stove(
              credentialId: 'chef_supermarket',
              tag:          scmInfo.tag
            )
          }else{
            println 'Not a tagged version, skipping publish'
          }
        }
      }
    }catch(error){
      // Archive kitchen logs to help debugging in case of failure
      archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: '.kitchen/logs/*.log'
      )

      // Setting notification errors
      notify  = true
      color   = "RED"
      result  = "FAILURE"
      message = output
      throw(error)
    }finally{
      // Notification stage
      stage('notification'){
        hipchatSend (
          color:        color,
          credentialId: 'jenkins-hipchat-token',
          message:      "Job Name: ${JOB_NAME} (<a href=\"${BUILD_URL}\">Open</a>)<br /> \
                         Job Status: ${result} <br /> \
                         Job Message: ${message}",
          room:         '942680',
          notify:       notify,
          sendAs:       'New-Jenkins',
          server:       'api.hipchat.com',
          v2enabled:    false
        )
      }

      // Result stage
      stage ('result'){
        junit '*_inspec.xml'
      }

      // Cleaning stage
      stage('cleaning'){
        cleanWs()
      }
    }
  }
}
