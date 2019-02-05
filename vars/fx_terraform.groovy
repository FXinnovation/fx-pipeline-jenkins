def call(Map config = [:]) {
  properties([
    disableConcurrentBuilds(),
    buildDiscarder(
      logRotator(
        artifactDaysToKeepStr: '',
        artifactNumToKeepStr: '10',
        daysToKeepStr: '',
        numToKeepStr: '10'
      )
    ),
    pipelineTriggers([[
      $class: 'PeriodicFolderTrigger',
      interval: '1d'
    ]])
  ])
//  buildCausers = currentBuild.getBuildCauses()


  if (!config.containsKey('initSSHCredentialId')) {
    config.initSSHCredentialId = 'gitea-fx_administrator-key'
  }
  if (!config.containsKey('initSSHHostKeys')) {
    config.initSSHHostKeys = [
      '|1|74mX2nLR+83nP0uyOZrs7NzEU1M=|RymcMBFFD+rBnGakSK5qWvCDJZs= ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDD4T37qQ3qJENiwfVwog8UfWlMg3Rl6PhEeF3vD1kvSpGjyF88XqgGsdDzTvFYeoc+WhbPQQXqIiowoovh6W4LkNi7SOTf10po0Llxde/xSZc32zQ4fltERf69L2XvQy43a5Apx1GgcLQrbaRj1/zx4Muo3hjvDu/OPkhso6Q734lfgZcy1uFoXaZIadeJOVzQIez3FiAmmr6r48Eb7hntK57u+Xdpd5Fq9zBDoMbzAnsCXZWzYEC/j9Hje+wV5iwyM/UWUaC06zyfG8NvPkwU90mIVxIX6NB9yrGlT0tPuNL69Vz4Ykc9LoHJQoHcetqbzS684vvwDnlXP0TrC/AV',
      '|1|6cz+5EZ5PkSoJsntX7GXerkhPps=|6oShAbhnZHc0/OnAg6zZ1nTsD2k= ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINnKb68pJWPerdWlT9m2DraaFalb7K562kUO4SHtpyaL',
      '|1|fTLlydQ8yCJSsm6XfMy1dO6e09E=|x6D/mfk5wbqg3t/li7vAIUsnA30= ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBAZ3HVe71Jte8O3B6CnnCojmCtQJidELmlSiKbxZphEwnhl6Wr7iF0GH+Oo5k34Q8toPHvmIRPh9UcNTr4g5dHI=',
    ]
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

        stageInit(config.terraformCommandTargets, config.initSSHCredentialId, config.initSSHHostKeys)

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
 * @param hostKeys
 */
def stageInit(ArrayList commandTargets, String credentialsId, ArrayList hostKeys){
  stage('init') {
    sshagent(['gitea-fx_administrator-key']) {
      sh("ssh-add -l")
      sh("mkdir -p ~/.ssh")
      sh('echo "' + hostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')
      execute(
        script: 'cat ~/.ssh/known_hosts'
      )
      for (commandTarget in commandTargets) {
        terraform.init(
          commandTarget: commandTarget,
          dockerAdditionalMounts: [
            '~/.ssh/': '/root/.ssh/',
            '\$(readlink -f $SSH_AUTH_SOCK)': '/ssh-agent',
          ],
          dockerEnvironmentVariables: [
            'SSH_AUTH_SOCK': '/ssh-agent'
          ]
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
      terraform.validate(
        commandTarget: commandTarget
      )
      terraform.fmt(
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
          plan(
            TF_access_key,
            TF_secret_key,
            commandTarget
          )

          terraform.apply(
            parallelism: 1,
            refresh: false,
            commandTarget: 'plan.out'
          )

          replay = plan(
            TF_access_key,
            TF_secret_key,
            commandTarget
          )

          if (!(replay.stdout =~ /.*Infrastructure is up-to-date.*/)) {
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
