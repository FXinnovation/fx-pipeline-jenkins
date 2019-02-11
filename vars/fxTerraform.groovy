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

  if (!config.containsKey('initSSHCredentialId') || !(config.providerAccessKeyVariableName instanceof CharSequence)) {
    // default: gitea administrator key
    config.initSSHCredentialId = 'gitea-fx_administrator-key'
  }
  if (!config.containsKey('planVars') || !(config.planVars instanceof List)) {
    config.planVars = []
  }
  if (!config.containsKey('initSSHHostKeys') || !(config.planVars instanceof List)) {
    // default: gitea host keys
    config.initSSHHostKeys = [
      '|1|V8Cs9iHiY6IHtysrmCUykgmnQEI=|CNbB5msCLZaO4ne4HAdyHNr93eo= ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDD4T37qQ3qJENiwfVwog8UfWlMg3Rl6PhEeF3vD1kvSpGjyF88XqgGsdDzTvFYeoc+WhbPQQXqIiowoovh6W4LkNi7SOTf10po0Llxde/xSZc32zQ4fltERf69L2XvQy43a5Apx1GgcLQrbaRj1/zx4Muo3hjvDu/OPkhso6Q734lfgZcy1uFoXaZIadeJOVzQIez3FiAmmr6r48Eb7hntK57u+Xdpd5Fq9zBDoMbzAnsCXZWzYEC/j9Hje+wV5iwyM/UWUaC06zyfG8NvPkwU90mIVxIX6NB9yrGlT0tPuNL69Vz4Ykc9LoHJQoHcetqbzS684vvwDnlXP0TrC/AV',
      '|1|+TYoo5BCu7BRQZl0+TXEK2f6JW8=|zfDEKwHmsNmNGPY1bNj1CNjOPLQ= ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBAZ3HVe71Jte8O3B6CnnCojmCtQJidELmlSiKbxZphEwnhl6Wr7iF0GH+Oo5k34Q8toPHvmIRPh9UcNTr4g5dHI=',
      '|1|6Kd9WNT+MUC06McJma+j91Fxfik=|aHMHcoEUNdmmgU75HWXfkIGTUWA= ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINnKb68pJWPerdWlT9m2DraaFalb7K562kUO4SHtpyaL',
    ]
  }
  if (!config.containsKey('commandTargets') || !(config.commandTargets instanceof List)) {
    config.commandTargets = ['.']
  }

  node {
    result="SUCCESS"
    try {
      ansiColor('xterm') {

        stageCheckout()

        for (commandTarget in config.commandTargets) {
          pipelineTerraform([
            commandTarget: commandTarget,
            testPlanOptions: [
              vars: config.planVars
            ],
            testDestroyOptions: [
              vars: config.planVars
            ],
          ], [
            init: {
              sshagent([config.initSSHCredentialId]) {
                sh('ssh-add -l')
                sh('mkdir -p ~/.ssh')
                sh('echo "' + config.initSSHHostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')
//                for (commandTarget in config.commandTargets) {
                terraform.init(
                  commandTarget: commandTarget,
                  dockerAdditionalMounts: [
                    '~/.ssh/'                       : '/root/.ssh/',
                    '\$(readlink -f $SSH_AUTH_SOCK)': '/ssh-agent',
                  ],
                  dockerEnvironmentVariables: [
                    'SSH_AUTH_SOCK': '/ssh-agent'
                  ]
                )
//                }
              }
            }
          ])
        }
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
