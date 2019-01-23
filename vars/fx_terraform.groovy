def call(Map config = [:]) {
  properties([
    disableConcurrentBuilds(),
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
      interval: '1d'
    ]])
  ])

  if (!config.containsKey('initCredentialId')) {
    config.initCredentialId = 'gitea-administrator'
  }
  if (!config.containsKey('testEnvironmentCredentialId')) {
    error('“testEnvironmentCredentialId” parameter is mandatory.')
  }
  if (!config.containsKey('terraformCommandTargets')) {
    config.terraformCommandTargets = ['.']
  }

  node {
    result="SUCCESS"
    try {
      ansiColor('xterm') {
        stageCheckout()

        stageInit(config.terraformCommandTargets, config.initCredentialId)

        stageValidate(config.terraformCommandTargets)

        stageTest(config.terraformCommandTargets, config.testEnvironmentCredentialId)
      }
    }catch (error){
      result='FAILURE'
      throw (error)
    }finally {
      stage('notify'){
        fx_notify(
          status: result
        )
      }
    }
  }
}

/**
 * Terraform plan.
 * @param username
 * @param password
 * @param commandTarget
 * @return String terraform plan output
 */
def plan(String username, String password, String commandTarget){
  return terraform.plan(
    out: 'plan.out',
    vars: [
      "access_key=${username}",
      "secret_key=${password}"
    ],
    commandTarget: commandTarget
  )
}


/**
 * Executes checkout stage.
 */
def stageCheckout(){
  stage('checkout') {
    scmInfo = fx_checkout()
  }
}

/**
 * Executes init stage.
 * @param commandTargets
 * @param credentialsId
 */
def stageInit(ArrayList commandTargets, String credentialsId){
  stage('init') {
    withCredentials([
      usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'git_user',
        passwordVariable: 'git_password'
      )
    ]) {
      sh("git config --global credential.helper '!f() { sleep 1; echo \"username=${git_user}\npassword=${git_password}\"; }; f'")
      println(sh(returnStdout: true, script: 'cat .git/config'))

      for (commandTarget in commandTargets) {
        println terraform.init(
          commandTarget: commandTarget
        )
      }
    }
  }
}

/**
 * Executes validate stage.
 * @param commandTargets
 */
def stageValidate(ArrayList commandTargets){
  stage('validate'){
    for ( commandTarget in commandTargets ) {
      println terraform.validate(
        commandTarget: commandTarget
      )
      println terraform.fmt(
        check: true,
        commandTarget: commandTarget
      )
    }
  }
}

/**
 * Executes test stage.
 * @param commandTargets
 * @param credentialsId
 */
def stageTest(ArrayList commandTargets, String credentialsId){
  stage('test') {
    withCredentials([
      usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'TF_access_key',
        passwordVariable: 'TF_secret_key'
      )
    ]) {
      for ( commandTarget in commandTargets ) {
        try {
          println plan(
            TF_access_key,
            TF_secret_key,
            commandTarget
          )

          println terraform.apply(
            parallelism: 1,
            refresh: false,
            commandTarget: 'plan.out'
          )

          replay = plan(
            TF_access_key,
            TF_secret_key,
            commandTarget
          )

          if (!(replay =~ /.*Infrastructure is up-to-date.*/)) {
            error('Replaying the “apply” contains new changes. Make sure your terraform consecutive run makes no changes.')
          }
        } catch (errorApply) {
          archiveArtifacts(
            allowEmptyArchive: true,
            artifacts: 'terraform.tfstat*'
          )
          throw (errorApply)
        } finally {
          terraform.destroy(
            vars: [
              "access_key=${TF_access_key}",
              "secret_key=${TF_secret_key}"
            ],
            commandTarget: commandTarget
          )
        }
      }
    }
  }
}