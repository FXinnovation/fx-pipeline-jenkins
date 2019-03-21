def call(Map config = [:]) {
  
  mapAttributeCheck(config, 'credentialSasKey', CharSequence, '', '“credentialSasKey” parameter is mandatory.')  
  mapAttributeCheck(config, 'storageAccountName', CharSequence, '', '“storageAccountName” parameter is mandatory.')  
  mapAttributeCheck(config, 'containerName', CharSequence, '', '“containerName” parameter is mandatory.')  
  mapAttributeCheck(config, 'localFilePath', CharSequence, '', '“localFilePath” parameter is mandatory.')  
  mapAttributeCheck(config, 'blobFilePath', CharSequence, '', '“blobFilePath” parameter is mandatory.')
  mapAttributeCheck(config, 'libFolder', CharSequence, 'fxinnovation-common-scripts-powershell')
  mapAttributeCheck(config, 'libVersion', CharSequence, '0.0.3')
  mapAttributeCheck(config, 'powershellDockerImage', CharSequence, 'fxinnovation/powershell:latest')
  mapAttributeCheck(config, 'filter', CharSequence, '*')
  mapAttributeCheck(config, 'deleteBeforeUpload', Boolean, false)
   
  def deleteBeforeUploadPowershell = '$False'
  
  if (config.deleteBeforeUpload) {
    deleteBeforeUploadPowershell = '$True'
  }
 

  fxCheckoutTag (
    directory: config.libFolder,
	credentialsId: "jenkins_fxinnovation_bitbucket",
	repoUrl: "https://bitbucket.org/fxadmin/fxinnovation-common-scripts-powershell.git",
	tag: config.libVersion
  )

  withCredentials([
    string(
      credentialsId: config.credentialSasKey,
      variable: 'sas_key'
    )
  ]) {
    executePowershell([
      dockerImage: config.powershellDockerImage,
      script: "/data/${config.libFolder}/AutomationAccount/Upload-Blob.ps1 -StorageAccountName \"${config.storageAccountName}\" -ContainerName \"${config.containerName}\" -LocalPath \"/data/${config.localFilePath}\" -RemotePath \"${config.blobFilePath}\" -SasToken \"${sas_key}\" -Filter \"${config.filter}\" -DeleteBeforeUpload ${deleteBeforeUploadPowershell} "
    ])
  }
}
