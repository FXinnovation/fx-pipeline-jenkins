import com.fxinnovation.nexus.PowershellModuleRepository

def addFXNexusRepository(Map config = [:]){
    mapAttributeCheck(config, 'rootFolder', CharSequence, '/data/', '')
    def readRepository = PowershellModuleRepository.getReadRepository(this,config)

    executePowershell([
        dockerImage: config.powershellDockerImage,
        script: "-command \"Add-PSRepositoryFile -File (Join-Path '${config.rootFolder}' 'Repositories.json') -Name 'FXNexus' -SourceLocation '${readRepository.getBaseUrl()}/';\""
    ])
}

def buildModule(Map config = [:]){
    mapAttributeCheck(config, 'powershellDockerImage', CharSequence, "fxinnovation/powershell-build",  '')

    mapAttributeCheck(config, 'rootFolder', CharSequence, '/data/', '')
    
    mapAttributeCheck(config, 'moduleName', CharSequence, '',  '“moduleName” parameter is mandatory.')
    mapAttributeCheck(config, 'version', CharSequence, '',  '“version” parameter is mandatory.')
    mapAttributeCheck(config, 'description', CharSequence, '',  '')

    addFXNexusRepository(config)

    try{
        executePowershell([
            dockerImage: config.powershellDockerImage,
            script: "-command \"Build-FXModule -RootFolder '${config.rootFolder}' -ModuleName '${config.moduleName}' -Version '${config.version}' -Description '${config.description}'\""
        ])
    }
    finally{
        nunit testResultsPattern: '_artefacts/**/test-result.xml'
    }
}

def buildApplication(Map config = [:]){
    mapAttributeCheck(config, 'powershellDockerImage', CharSequence, "fxinnovation/powershell-build",  '')

    mapAttributeCheck(config, 'rootFolder', CharSequence, '/data/', '')
    mapAttributeCheck(config, 'applicationName', CharSequence, '',  '“applicationName” parameter is mandatory.')

    addFXNexusRepository(config)

    try{
        executePowershell([
            dockerImage: config.powershellDockerImage,
            script: "-command \"Build-FXApplication -RootFolder '${config.rootFolder}' -ApplicationName '${config.applicationName}'\""
        ])
    }
    finally{
        nunit testResultsPattern: '_artefacts/**/test-result.xml'
    }
}

def publishModule(Map config = [:]){  
    mapAttributeCheck(config, 'powershellDockerImage', CharSequence, "fxinnovation/powershell-build",  '')  
    
    mapAttributeCheck(config, 'rootFolder', CharSequence, '/data/', '')
    mapAttributeCheck(config, 'nuGetApiKey', CharSequence, '',  'nuGetApiKey must be set')
    mapAttributeCheck(config, 'publishRepository', PowershellModuleRepository, '',  'publishRepository must be set')

    withCredentials([string(credentialsId: config.nuGetApiKey, variable: 'mysecret')]){
        executePowershell([
            dockerImage: config.powershellDockerImage,
            script: "-command \"Publish-FXModule -RootFolder '${config.rootFolder}' -PublishUri '${config.publishRepository.getBaseUrl()}/' -NuGetApiKey '${mysecret}'\""
        ])
    }
}