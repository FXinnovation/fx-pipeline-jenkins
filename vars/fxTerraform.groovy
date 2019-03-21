def call(Map config = [:]) {
  mapAttributeCheck(config, 'initSSHCredentialId', CharSequence, 'gitea-fx_administrator-key')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])
  mapAttributeCheck(config, 'initSSHHostKeys', List, [
    '|1|V8Cs9iHiY6IHtysrmCUykgmnQEI=|CNbB5msCLZaO4ne4HAdyHNr93eo= ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDD4T37qQ3qJENiwfVwog8UfWlMg3Rl6PhEeF3vD1kvSpGjyF88XqgGsdDzTvFYeoc+WhbPQQXqIiowoovh6W4LkNi7SOTf10po0Llxde/xSZc32zQ4fltERf69L2XvQy43a5Apx1GgcLQrbaRj1/zx4Muo3hjvDu/OPkhso6Q734lfgZcy1uFoXaZIadeJOVzQIez3FiAmmr6r48Eb7hntK57u+Xdpd5Fq9zBDoMbzAnsCXZWzYEC/j9Hje+wV5iwyM/UWUaC06zyfG8NvPkwU90mIVxIX6NB9yrGlT0tPuNL69Vz4Ykc9LoHJQoHcetqbzS684vvwDnlXP0TrC/AV',
    '|1|+TYoo5BCu7BRQZl0+TXEK2f6JW8=|zfDEKwHmsNmNGPY1bNj1CNjOPLQ= ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBAZ3HVe71Jte8O3B6CnnCojmCtQJidELmlSiKbxZphEwnhl6Wr7iF0GH+Oo5k34Q8toPHvmIRPh9UcNTr4g5dHI=',
    '|1|6Kd9WNT+MUC06McJma+j91Fxfik=|aHMHcoEUNdmmgU75HWXfkIGTUWA= ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINnKb68pJWPerdWlT9m2DraaFalb7K562kUO4SHtpyaL',
  ])
  mapAttributeCheck(config, 'commandTargets', List, ['.'])

  fxJob([
    pipeline: {
      for (commandTarget in config.commandTargets) {
        pipelineTerraform([
          commandTarget     : commandTarget,
          testPlanOptions   : [
            vars: config.testPlanVars
          ],
          validateOptions   : [
            vars: config.validateVars
          ],
          testDestroyOptions: [
            vars: config.testPlanVars
          ],
        ], [
          init: {
            sshagent([config.initSSHCredentialId]) {
              sh('ssh-add -l')
              sh('mkdir -p ~/.ssh')
              sh('echo "' + config.initSSHHostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')
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
            }
          }
        ])
      }
    }
  ], [
      disableConcurrentBuilds(),
      buildDiscarder(
        logRotator(
          artifactDaysToKeepStr: '',
          artifactNumToKeepStr: '10',
          daysToKeepStr: '',
          numToKeepStr: '10'
        )
      ),
      pipelineTriggers([
        [
          $class  : 'PeriodicFolderTrigger',
          interval: '1d'
        ]
      ])
    ]
  )
}
