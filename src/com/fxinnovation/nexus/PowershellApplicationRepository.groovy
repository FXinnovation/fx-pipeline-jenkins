package com.fxinnovation.nexus

class PowershellApplicationRepository extends NexusRepository {
    public Boolean isRelease
    private PowershellApplicationRepository(String nexusUrl,String repository,Boolean isRelease){
        super(nexusUrl,repository);

        this.isRelease = isRelease
    }

    public static PowershellApplicationRepository getReleaseRepository(groovy.lang.Script context, config){
        context.mapAttributeCheck(config, 'nexusUrl', CharSequence, NexusDefaultValues.NexusUrl)
        context.mapAttributeCheck(config, 'powershellApplicationReleaseRepository', CharSequence, NexusDefaultValues.PowershellApplicationReleaseRepository)
    
        return new PowershellApplicationRepository(config.nexusUrl,config.powershellApplicationReleaseRepository,true)
    }

    public static PowershellApplicationRepository getUnstableRepository(groovy.lang.Script context, config){
        context.mapAttributeCheck(config, 'nexusUrl', CharSequence, NexusDefaultValues.NexusUrl)
        context.mapAttributeCheck(config, 'powershellApplicationUnstableRepository', CharSequence, NexusDefaultValues.PowershellApplicationUnstableRepository)
    
        return new PowershellApplicationRepository(config.nexusUrl,config.powershellApplicationUnstableRepository,false)
    }

    public PowershellApplicationItem newItem(groovy.lang.Script context, config){
        context.mapAttributeCheck(config, 'client', CharSequence, NexusDefaultValues.MavenDefaultGroupId)
        context.mapAttributeCheck(config, 'applicationName', CharSequence, '',  'You need to privide an applicationName')
        context.mapAttributeCheck(config, 'version', CharSequence, '',  'You need to privide a version')        
        context.mapAttributeCheck(config, 'asset', CharSequence, config.applicationName)

        return new PowershellApplicationItem(
            this,
            config.client.toLowerCase(),
            config.applicationName.toLowerCase(),
            config.version,
            config.asset
        )
    }

    public static PowershellApplicationItem getItemFromMap(groovy.lang.Script context, Map config){
        context.mapAttributeCheck(config, 'isRelease', Boolean, true)

        def repository = config.isRelease ? getReleaseRepository(context,config) : getUnstableRepository(context,config)

        return repository.newItem(context,config)
    }
}