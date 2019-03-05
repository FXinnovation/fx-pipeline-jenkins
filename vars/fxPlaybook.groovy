def call() {
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

  notify      = false
  color       = "GREEN"
  result      = "SUCCESS"
  message     = "Build finished"
  
  node() {
    try {
      stage('pre-build'){
        fxCheckout()
      }

      stage ('ansible-lint'){
        try {
          ansiblelint()
        } catch(error) {
          writeFile(
            file: 'ansible-lint.txt',
            text: error.getMessage()
          )
          throw(error)
        }
      }
    } catch(error) {
      archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: 'ansible-lint.txt'
      )
      notify  = true
      color   = "RED"
      result  = "FAILURE"
      message = error
      throw(error)
    } finally {
      stage('notification') {
        fx_notify(
          status: result
        )
      }

      stage('result'){
        def issues = scanForIssues blameDisabled: true, tool: pyLint(name: 'ansible-lint', pattern: 'ansible-lint.txt')
        publishIssues issues: [issues]
      }

      stage('cleaning') {
        cleanWs()
      }
    }
  }
}
