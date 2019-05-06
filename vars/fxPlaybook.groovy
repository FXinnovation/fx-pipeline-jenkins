def call(Map config = [:], Map closures = [:]) {
  mapAttributeCheck(config, 'ansiblelintConfig',      Map,          [:])
  mapAttributeCheck(config, 'ansiblelintOutputFile',  CharSequence, 'ansible-lint.txt')
  mapAttributeCheck(config, 'awsIAMProfile',          CharSequence, 'AmazonEC2ReadOnlyAccess')
  mapAttributeCheck(config, 'awsRegion',              CharSequence, 'us-east-1')
  mapAttributeCheck(config, 'awsSubnetId',            CharSequence, 'subnet-cd28dca9')
  mapAttributeCheck(config, 'kitchenAwsCredentialId', CharSequence, 'itoa-application-awscollectors-awscred')
  mapAttributeCheck(config, 'kitchenAnsibleVersion',  CharSequence, '2.7.10')
  mapAttributeCheck(config, 'kitchenConcurrency',     Integer,      5)
  mapAttributeCheck(config, 'kitchenIdempotency',     CharSequence, 'true')
  mapAttributeCheck(config, 'kitchenSshCredentialId', CharSequence, 'fxlab_jenkins')
  mapAttributeCheck(config, 'junitReport',            CharSequence, '*_inspec.xml')
  mapAttributeCheck(config, 'scmSshCredentialId',     CharSequence, 'gitea-fx_administrator-key')
  mapAttributeCheck(config, 'publish',                Boolean,      false)
  mapAttributeCheck(config, 'initSSHHostKeys',        List,         [
    '|1|V8Cs9iHiY6IHtysrmCUykgmnQEI=|CNbB5msCLZaO4ne4HAdyHNr93eo= ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDD4T37qQ3qJENiwfVwog8UfWlMg3Rl6PhEeF3vD1kvSpGjyF88XqgGsdDzTvFYeoc+WhbPQQXqIiowoovh6W4LkNi7SOTf10po0Llxde/xSZc32zQ4fltERf69L2XvQy43a5Apx1GgcLQrbaRj1/zx4Muo3hjvDu/OPkhso6Q734lfgZcy1uFoXaZIadeJOVzQIez3FiAmmr6r48Eb7hntK57u+Xdpd5Fq9zBDoMbzAnsCXZWzYEC/j9Hje+wV5iwyM/UWUaC06zyfG8NvPkwU90mIVxIX6NB9yrGlT0tPuNL69Vz4Ykc9LoHJQoHcetqbzS684vvwDnlXP0TrC/AV',
    '|1|+TYoo5BCu7BRQZl0+TXEK2f6JW8=|zfDEKwHmsNmNGPY1bNj1CNjOPLQ= ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBAZ3HVe71Jte8O3B6CnnCojmCtQJidELmlSiKbxZphEwnhl6Wr7iF0GH+Oo5k34Q8toPHvmIRPh9UcNTr4g5dHI=',
    '|1|6Kd9WNT+MUC06McJma+j91Fxfik=|aHMHcoEUNdmmgU75HWXfkIGTUWA= ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINnKb68pJWPerdWlT9m2DraaFalb7K562kUO4SHtpyaL',
  ])

  fxJob([
    pipeline: { Map scmInfo ->
      withCredentials([
        sshUserPrivateKey(
          credentialsId:   config.scmSshCredentialId,
          keyFileVariable: 'scmKey'
        ),
        sshUserPrivateKey(
          credentialsId:   config.kitchenSshCredentialId,
          keyFileVariable: 'kitchenKey'
        ),
        usernamePassword(
          credentialsId:    config.kitchenAwsCredentialId,
          usernameVariable: 'access_key',
          passwordVariable: 'secret_key'
        )
      ]) {
        sshagent([config.scmSshCredentialId]) {
          if (!closures.containsKey('converge')){
            closures.converge = {
              try {
                kitchen.test(
                  dockerEnvironmentVariables: [
                    AWS_SSH_KEY_ID:          "${config.kitchenSshCredentialId}",
                    AWS_SSH_KEY:             '/id_rsa.pem',
                    AWS_ACCESS_KEY_ID:       "${access_key}",
                    AWS_SECRET_ACCESS_KEY:   "${secret_key}",
                    AWS_REGION:              "${config.awsRegion}",
                    AWS_SUBNETID:            "${config.awsSubnetId}",
                    AWS_IAMPROFILE:          "${config.awsIAMProfile}",
                    KITCHEN_IDEMPOTENCY:     "${config.kitchenIdempotency}",
                    KITCHEN_ANSIBLE_VERSION: "${config.kitchenAnsibleVersion}",
                    KITCHEN_ROLENAME:        "${scmInfo.repositoryName}"
                  ],
                  dockerAdditionalMounts: [
                    (kitchenKey): '/id_rsa.pem'
                  ],
                  concurrency: config.kitchenConcurrency
                )
              } catch(kitchenError) {
                archiveArtifacts(
                  allowEmptyArchive: false,
                  artifacts:         '.kitchen/**/*'
                )
                throw(kitchenError)
              } finally {
                archiveArtifacts(
                  allowEmptyArchive: true,
                  artifacts:         config.junitReport
                )
                junit config.junitReport
              }
            }
          }

          pipelinePlaybook(
            [
              ansiblelintConfig:     config.ansiblelintConfig,
              galaxySSHHostKeys:     config.initSSHHostKeys,
              galaxyAgentSocket:     '\$(readlink -f $SSH_AUTH_SOCK)',
              awsSSHKeyId:           "${config.kitchenSshCredentialId}",
              awsAccessKeyId:        "${access_key}",
              awsSecretAccessKey:    "${secret_key}",
              awsRegion:             "${config.awsRegion}",
              awsSubnetId:           "${config.awsSubnetId}",
              awsIAMProfile:         "${config.awsIAMProfile}",
              kitchenIdempotency:    "${config.kitchenIdempotency}",
              kitchenAnsibleVersion: "${config.kitchenAnsibleVersion}",
              kitchenPrivateKeyPath: "${kitchenKey}",
              publish:               config.publish
            ],
            closures
          )
        }
      }
    },
    preNotify: {
      def issues = scanForIssues(
        tool: pyLint(
          name: 'ansible-lint',
          pattern: config.ansiblelintOutputFile
        )
      )
      publishIssues issues: [issues]
    }
  ])
}
