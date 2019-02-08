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
    // default: gitea administrator key
    config.initSSHCredentialId = 'gitea-fx_administrator-key'
  }
  if (!config.containsKey('testEnvironmentCredentialId')) {
    error('“testEnvironmentCredentialId” parameter is mandatory.')
  }
  if (!config.containsKey('initSSHHostKeys')) {
    // default: gitea host keys
    config.initSSHHostKeys = [
      '|1|74mX2nLR+83nP0uyOZrs7NzEU1M=|RymcMBFFD+rBnGakSK5qWvCDJZs= ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDD4T37qQ3qJENiwfVwog8UfWlMg3Rl6PhEeF3vD1kvSpGjyF88XqgGsdDzTvFYeoc+WhbPQQXqIiowoovh6W4LkNi7SOTf10po0Llxde/xSZc32zQ4fltERf69L2XvQy43a5Apx1GgcLQrbaRj1/zx4Muo3hjvDu/OPkhso6Q734lfgZcy1uFoXaZIadeJOVzQIez3FiAmmr6r48Eb7hntK57u+Xdpd5Fq9zBDoMbzAnsCXZWzYEC/j9Hje+wV5iwyM/UWUaC06zyfG8NvPkwU90mIVxIX6NB9yrGlT0tPuNL69Vz4Ykc9LoHJQoHcetqbzS684vvwDnlXP0TrC/AV',
      '|1|6cz+5EZ5PkSoJsntX7GXerkhPps=|6oShAbhnZHc0/OnAg6zZ1nTsD2k= ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINnKb68pJWPerdWlT9m2DraaFalb7K562kUO4SHtpyaL',
      '|1|fTLlydQ8yCJSsm6XfMy1dO6e09E=|x6D/mfk5wbqg3t/li7vAIUsnA30= ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBAZ3HVe71Jte8O3B6CnnCojmCtQJidELmlSiKbxZphEwnhl6Wr7iF0GH+Oo5k34Q8toPHvmIRPh9UcNTr4g5dHI=',
    ]
  }
  if (!config.containsKey('terraformCommandTargets') || !(config.terraformCommandTargets instanceof List)) {
    config.terraformCommandTargets = ['.']
  }

  node {
    result="SUCCESS"
    try {
      ansiColor('xterm') {

        stageCheckout()

        println config.terraformCommandTargets

        pipelineTerraform([
            commandTargets: config.terraformCommandTargets,
            testPlanOptions: [
              vars: [
                'access_key=\$TF_access_key',
                'secret_key=\$TF_secret_key',
              ]
            ],
            testDestroyOptions: [
              vars: [
                'access_key=\$TF_access_key',
                'secret_key=\$TF_secret_key',
              ]
            ],
          ], [
            preTest: {
              withCredentials([
                usernamePassword(
                  credentialsId: config.testEnvironmentCredentialId,
                  usernameVariable: 'TF_access_key',
                  passwordVariable: 'TF_secret_key'
                )
              ]) {
                execute(
                  script: "export TF_access_key=${TF_access_key}"
                )
                execute(
                  script: "export TF_secret_key=${TF_secret_key}"
                )
              }
            },
            init: {
              sshagent([config.initSSHCredentialId]) {
                execute(
                  script: 'ssh-add -l'
                )
                execute(
                  script:  'mkdir -p ~/.ssh'
                )
                execute(
                  script:  'echo "' + config.hostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts'
                )
                execute(
                  script: 'cat ~/.ssh/known_hosts'
                )
                for (commandTarget in config.commandTargets) {
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
          ]
        )
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
