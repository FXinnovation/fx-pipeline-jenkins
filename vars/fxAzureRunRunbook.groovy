def call(Map config = [:]) {

  mapAttributeCheck(config, 'credentialAzure', CharSequence, '', '“credentialAzure” parameter is mandatory.')
  mapAttributeCheck(config, 'resourceGroupName', CharSequence, '', '“resourceGroupName” parameter is mandatory.')
  mapAttributeCheck(config, 'runbook', CharSequence, '', '“runbook” parameter is mandatory.')
  mapAttributeCheck(config, 'automationAccountName', CharSequence, '', '“automationAccountName” parameter is mandatory.')
  mapAttributeCheck(config, 'tenantId', CharSequence, '', '“tenantId” parameter is mandatory.')
  mapAttributeCheck(config, 'libFolder', CharSequence, 'fxinnovation-common-scripts-powershell')
  mapAttributeCheck(config, 'libVersion', CharSequence, '0.0.16')
  mapAttributeCheck(config, 'powershellDockerImage', CharSequence, 'fxinnovation/powershell:latest')
  mapAttributeCheck(config, 'runbookOptions', CharSequence, '')

  if('' != config.runbookOptions) {
    config.runbookOptions = "-Parameters \"${config.runbookOptions}\""
  }

  // This is temporary since we need to create nugget repo
  fxCheckoutTag (
    directory: config.libFolder,
    credentialsId: 'jenkins_fxinnovation_bitbucket',
    repoUrl: 'https://bitbucket.org/fxadmin/fxinnovation-common-scripts-powershell.git',
    tag: config.libVersion
  )
  withCredentials([
    usernamePassword(
      credentialsId: config.credentialAzure,
      usernameVariable: 'appId',
      passwordVariable: 'servicePrincipalPassword'
    )
  ]) {
    executePowershell([
      dockerImage: config.powershellDockerImage,
      script: """/data/${config.libFolder}/AutomationAccount/Run-Runbook.ps1 
                 -ResourceGroupName \"${config.resourceGroupName}\" 
                 -Runbook \"${config.runbook}\" 
                 -AutomationAccountName \"${config.automationAccountName}\" 
                 -Tenant \"${config.tenantId}\" 
                 -AppId \"${appId}\" 
                 -Password \"${servicePrincipalPassword}\" 
                 ${config.runbookOptions} 
                 -Verbose """
    ])
  }
}

