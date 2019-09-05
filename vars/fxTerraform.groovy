def call(Map config = [:], Map closures = [:]) {
  mapAttributeCheck(closures, 'postPrepare', Closure, {})
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])
  mapAttributeCheck(config, 'initSSHCredentialId', CharSequence, 'gitea-fx_administrator-key')
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'initSSHHostKeys', List, [
    '|1|V8Cs9iHiY6IHtysrmCUykgmnQEI=|CNbB5msCLZaO4ne4HAdyHNr93eo= ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDD4T37qQ3qJENiwfVwog8UfWlMg3Rl6PhEeF3vD1kvSpGjyF88XqgGsdDzTvFYeoc+WhbPQQXqIiowoovh6W4LkNi7SOTf10po0Llxde/xSZc32zQ4fltERf69L2XvQy43a5Apx1GgcLQrbaRj1/zx4Muo3hjvDu/OPkhso6Q734lfgZcy1uFoXaZIadeJOVzQIez3FiAmmr6r48Eb7hntK57u+Xdpd5Fq9zBDoMbzAnsCXZWzYEC/j9Hje+wV5iwyM/UWUaC06zyfG8NvPkwU90mIVxIX6NB9yrGlT0tPuNL69Vz4Ykc9LoHJQoHcetqbzS684vvwDnlXP0TrC/AV',
    '|1|+TYoo5BCu7BRQZl0+TXEK2f6JW8=|zfDEKwHmsNmNGPY1bNj1CNjOPLQ= ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBAZ3HVe71Jte8O3B6CnnCojmCtQJidELmlSiKbxZphEwnhl6Wr7iF0GH+Oo5k34Q8toPHvmIRPh9UcNTr4g5dHI=',
    '|1|6Kd9WNT+MUC06McJma+j91Fxfik=|aHMHcoEUNdmmgU75HWXfkIGTUWA= ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINnKb68pJWPerdWlT9m2DraaFalb7K562kUO4SHtpyaL',
  ])
  mapAttributeCheck(config, 'terraformInitBackendConfigsTest', ArrayList, [])
  mapAttributeCheck(config, 'terraformInitBackendConfigsPublish', ArrayList, [])
  mapAttributeCheck(config, 'commonOptions', Map, [:])

  // commandTargets is deprecated - to be removed once Jenkinsfile are update not to contain commandTargets.
  if (config.containsKey('commandTargets')) {
    print('DEPRECATED WARNING: please remove “commandTargets” attribute from your Jenkinsfile as it’s not used anymore. Once all Jenkinsfiles are updated, remove this message.')
  }

  fxJob([
    postPrepare: closures.postPrepare,
    pipeline: { Map scmInfo ->
      def isTagged = '' != scmInfo.tag
      def deployFileExists = fileExists 'deploy.tf'
      def toDeploy = false

      if (isTagged && deployFileExists && jobInfo.isManuallyTriggered()){
        toDeploy = true
      }

      printDebug("isTagged: ${isTagged} | deployFileExists: ${deployFileExists} | manuallyTriggered: ${jobInfo.isManuallyTriggered()} | toDeploy: ${toDeploy}")

      commandTargets = []
      try {
        execute(script: "[ -d examples ]")

        for (commandTarget in execute(script: "ls examples | sed -e 's/.*/examples\\/\\0/g'").stdout.split()) {
          commandTargets += commandTarget
        }
      } catch (error) {
        commandTargets = ['.']
      }

      fmt(config, commandTargets)

      printDebug('commandTargets: ' + commandTargets)

      for(commandTarget in commandTargets) {
        pipelineTerraform(
          config +
          [
            commandTarget     : commandTarget,
            testPlanOptions   : [
              vars: config.testPlanVars
            ] + config.commonOptions,
            testApplyOptions : config.commonOptions,
            fmtOptions: config.commonOptions,
            validateOptions   : [
              vars: config.validateVars
            ] + config.commonOptions,
            testDestroyOptions: [
              vars: config.testPlanVars
            ] + config.commonOptions,
            validateOptions   : [
              vars: config.testPlanVars
            ] + config.commonOptions,
            publish           : deployFileExists
          ], [
            preValidate: { preValidate(deployFileExists, scmInfo) },
            init       : { init(config, commandTarget, deployFileExists) },
            publish    : { publish(config, commandTarget, toDeploy, deployFileExists) }
          ]
        )
      }
    },
    postNotify: {
      if (config.containsKey('commandTargets')) {
        print('DEPRECATED WARNING: please remove “commandTargets” attribute from your Jenkinsfile as it’s not used anymore. Once all Jenkinsfiles are updated, remove this message.')
      }
    }
  ], [
    disableConcurrentBuilds()
  ])
}

private preValidate(Boolean deployFileExists, Map scmInfo) {
  if (!deployFileExists) {
    if (!fileExists('main.tf')) {
      error("This build does not meet FX standards: a Terraform module MUST contain a “main.tf” file. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#modules.")
    }
    if (!fileExists('variables.tf')) {
      error("This build does not meet FX standards: a Terraform module MUST contain a “variables.tf” file. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#modules.")
    }
    if (!fileExists('outputs.tf')) {
      error("This build does not meet FX standards: a Terraform module MUST contain a “outputs.tf” file. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#modules.")
    }
    if (!fileExists('README.md')) {
      error("This build does not meet FX standards: a Terraform module MUST contain a “README.md” file. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#modules.")
    }
    if (!fileExists('.gitignore')) {
      error("This build does not meet FX standards: a Terraform module MUST contain a “.gitignore” file. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#modules.")
    }

    if (!(scmInfo.repositoryName ==~ /^terraform\-(module|ecosystem)\-(aws|azurerm|azuread|google|bitbucket|gitlab|github|kubernetes|multi)-[a-z\d]{3,}([a-z\d\-]+)?$/)) {
      error("This build does not meet FX standards: a Terraform module MUST be name “terraform-*(module|ecosystem)*-*provider*-*name-with-hyphens*”. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#repositories.")
    }
  }

  if (deployFileExists) {
    for (filename in execute(script: "ls").stdout.split()) {
      if (filename =~ /.+\.tf$/ && 'deploy.tf' != filename && 'variables.tf' != filename && 'outputs.tf' != filename) {
        error("The current build is a candidate to publish but it contains a “${filename}” file. This does not comply with FX standard. For deployments, create a single “deploy.tf” with a “variables.tf”/“outputs.tf” files.")
      }
    }

    if (!(scmInfo.repositoryName ==~ /^terraform\-deployment\-[a-z\d-]{3,}$/)) {
      error("This build does not meet FX standards: a Terraform deployment MUST be name “terraform-deployment-*name-with-hyphens*”. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#repositories.")
    }
  }
}

private fmt(Map config = [:], List commandTargets) {
  if ('.' == commandTargets[0]) {
    return
  }

  printDebug('Global format to workaround a Terraform bug making fmt pass even if some sub-modules are incorrectly formatted.')

  pipelineTerraform.fmt('.')
}

private init(Map config = [:], CharSequence commandTarget, Boolean deployFileExists) {
  sshagent([config.initSSHCredentialId]) {
    sh('ssh-add -l')
    sh('mkdir -p ~/.ssh')
    sh('echo "' + config.initSSHHostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')
    terraform.init([
      commandTarget: commandTarget,
      dockerAdditionalMounts: [
        '~/.ssh/': '/root/.ssh/',
        '\$(readlink -f $SSH_AUTH_SOCK)': '/ssh-agent',
      ],
      dockerEnvironmentVariables: [
        'SSH_AUTH_SOCK': '/ssh-agent',
      ],
      backendConfigs: deployFileExists ? config.terraformInitBackendConfigsPublish : config.terraformInitBackendConfigsTest
    ] + config.commonOptions)
  }
}

private publish(Map config = [:], CharSequence commandTarget, Boolean toDeploy, Boolean deployFileExists) {
  plan = terraform.plan([
    commandTarget: commandTarget,
    out: 'plan.out',
    vars: config.publishPlanVars
  ] + config.commonOptions)

  if (deployFileExists) {
    terraform.show([
      commandTarget: 'plan.out',
    ] + config.commonOptions)
  }

  if (plan.stdout =~ /.*Infrastructure is up-to-date.*/) {
    println('The “plan” does not contain new changes. Infrastructure is up-to-date.')
    return
  }

  if (!toDeploy) {
    println('The code is either not tagged or the pipeline was triggered automatically. Skipping deployment.')
    return
  }

  fx_notify(
    status: 'PENDING'
  )

  timeout(activity: true, time: 20) {
    input 'WARNING: You are about to deploy the displayed plan in. Do you want to apply it?'
  }

  terraform.apply([
    commandTarget: 'plan.out'
  ] + config.commonOptions)
}
