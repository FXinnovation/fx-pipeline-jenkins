def call(Map config = [:], Map closures = [:]) {
  mapAttributeCheck(config, 'ansiblelintConfig',     Map, [:])
  mapAttributeCheck(config, 'ansiblelintOutputFile', CharSequence, 'ansible-lint.txt')

  if (!closures.containsKey('converge')){
    mapAttributeCheck(config, 'awsSSHKeyId',           CharSequence, '', '“awsSSHKeyId” parameter is mandatory without custom closure “converge”.')
    mapAttributeCheck(config, 'awsAccessKeyId',        CharSequence, '', '“awsAccessKeyId” parameter is mandatory without custom closure “converge”.')
    mapAttributeCheck(config, 'awsSecretAccessKey',    CharSequence, '', '“awsSecretAccessKey” parameter is mandatory without custom closure “converge”.')
    mapAttributeCheck(config, 'awsRegion',             CharSequence, '', '“awsRegion” parameter is mandatory without custom closure “converge”.')
    mapAttributeCheck(config, 'awsSubnetId',           CharSequence, '', '“awsSubnetId” parameter is mandatory without custom closure “converge”.')
    mapAttributeCheck(config, 'awsIAMProfile',         CharSequence, '', '“awsIAMProfile” parameter is mandatory without custom closure “converge”.')
    mapAttributeCheck(config, 'kitchenIdempotency',    CharSequence, '', '“kitchenIdempotency” parameter is mandatory without custom closure “converge”.')
    mapAttributeCheck(config, 'kitchenAnsibleVersion', CharSequence, 'latest')
    mapAttributeCheck(config, 'kitchenPrivateKeyPath', CharSequence, '', '“kitchenPrivateKeyPath” parameter is mandatory without custom closure “converge”.')

    closures.converge = {
      kitchen.test(
        dockerEnvironmentVariables: [
          AWS_SSH_KEY_ID:          "${config.awsSSHKeyId}",
          AWS_SSH_KEY:             '/id_rsa.pem',
          AWS_ACCESS_KEY_ID:       "${config.awsAccessKeyId}",
          AWS_SECRET_ACCESS_KEY:   "${config.awsSecretAccessKey}",
          AWS_REGION:              "${config.awsRegion}",
          AWS_SUBNETID:            "${config.awsSubnetId}",
          AWS_IAMPROFILE:          "${config.awsIAMProfile}",
          KITCHEN_IDEMPOTENCY:     "${config.kitchenIdempotency}",
          KITCHEN_ANSIBLE_VERSION: "${config.kitchenAnsibleVersion}",
          KITCHEN_ROLENAME:        "${config.kitchenRoleName}"
        ],
        dockerAdditionalMounts: [
          (config.kitchenPrivateKeyPath): '/id_rsa.pem'
        ]
      )
    }
  }

  fxJob([
    pipeline: { Map scmInfo ->
      config.kitchenRoleName = "${scmInfo.repositoryName}"
      pipelinePlaybook(config, closures)
    },
    preNotify: {
      def issues = scanForIssues(
        blameDisabled: true,
        tool: pyLint(
          name: 'ansible-lint',
          pattern: config.ansiblelintOutputFile
        )
      )
      publishIssues issues: [issues]
    }
  ])
}
