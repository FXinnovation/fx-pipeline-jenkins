def call(Map config = [:]) {

  mapAttributeCheck(config, 'credentialAzure', CharSequence, '', '“credentialAzure” parameter is mandatory.')
  mapAttributeCheck(config, 'resourceGroupName', CharSequence, '', '“resourceGroupName” parameter is mandatory.')
  mapAttributeCheck(config, 'vitualMachineName', CharSequence, '', '“virtualMachineName” parameter is mandatory.')
  mapAttributeCheck(config, 'scriptName', CharSequence, '', '“scriptName” parameter is mandatory.')
  mapAttributeCheck(config, 'tenantId', CharSequence, '', '“tenantId” parameter is mandatory.')
  mapAttributeCheck(config, 'libFolder', CharSequence, 'fxinnovation-common-scripts-powershell')
  mapAttributeCheck(config, 'libVersion', CharSequence, '0.0.21')
  mapAttributeCheck(config, 'powershellDockerImage', CharSequence, 'fxinnovation/powershell:latest')
  mapAttributeCheck(config, 'scriptParameters', CharSequence, '')

  if('' != config.scriptParameters) {
    config.scriptParameters = "-Parameters \"${config.scriptParameters}\""
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
      script: "/data/${config.libFolder}/CodeSnipetCICD/Run-AzureCommand.ps1 -ResourceGroupName \"${config.resourceGroupName}\" -VirtualMachineName \"${config.vitualMachineName}\" -ScriptPath \"/data/${config.scriptName}\" -Tenant \"${config.tenantId}\" -AppId \"${appId}\" -Password \"${servicePrincipalPassword}\" ${config.scriptParameters} -Verbose "
    ])
  }
}
