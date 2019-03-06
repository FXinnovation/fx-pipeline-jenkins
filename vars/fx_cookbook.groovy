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
      ansiColor('xterm') {
        stage('pre-build'){
          scmInfo = fx_checkout()
          sh 'ssh-keygen -t rsa -f /tmp/id_rsa -P \'\''
        }

        stage ('foodcritic'){
          output = foodcritic()
        }

        stage ('cookstyle'){
          output = cookstyle()
        }

        stage ('kitchen') {
          output = kitchen()
        }

        stage ('publish') {
          if (scmInfo.tag != ''){
            output = stove(
              credentialsId: 'chef_supermarket'
            )
          }else{
            println 'Not a tagged version, skipping publish'
          }
        }
      }
    }catch(error){
      archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: '.kitchen/logs/*.log'
      )
      notify  = true
      color   = "RED"
      result  = "FAILURE"
      message = error
      throw(error)
    }finally{
      stage('notification'){
        fx_notify(
          status: result
        )
      }

      stage ('result'){
        junit '*_inspec.xml'
      }

      stage('cleaning'){
        command("docker run --rm -v /tmp:/tmp -v \$(pwd):/data -w /data fxinnovation/chefdk:latest kitchen destroy -c 10")
        cleanWs()
      }
    }
  }
}
