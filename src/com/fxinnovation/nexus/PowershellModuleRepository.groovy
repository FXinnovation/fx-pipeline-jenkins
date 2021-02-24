/****************************************
// This file should not be used anymore
// Obsolete
*****************************************/

package com.fxinnovation.nexus

class PowershellModuleRepository extends NexusRepository {
    private PowershellModuleRepository(String nexusUrl,String repository){
        super(nexusUrl,repository);
    }

    public static PowershellModuleRepository getReleaseRepository(groovy.lang.Script context, config){
        context.mapAttributeCheck(config, 'nexusUrl', CharSequence, NexusDefaultValues.NexusUrl)
        context.mapAttributeCheck(config, 'powershellModuleReleaseRepository', CharSequence, NexusDefaultValues.PowershellModuleReleaseRepository)

        return new PowershellModuleRepository(config.nexusUrl,config.powershellModuleReleaseRepository)
    }

    public static PowershellModuleRepository getUnstableRepository(groovy.lang.Script context, config){
        context.mapAttributeCheck(config, 'nexusUrl', CharSequence, NexusDefaultValues.NexusUrl)
        context.mapAttributeCheck(config, 'powershellModulePublishUnstableRepository', CharSequence, NexusDefaultValues.PowershellModuleUnstableRepository)

        return new PowershellModuleRepository(config.nexusUrl,config.powershellModulePublishUnstableRepository)
    }

    public static PowershellModuleRepository getReadRepository(groovy.lang.Script context, config){
        context.println('----- getReadRepository -----')

        context.mapAttributeCheck(config, 'nexusUrl', CharSequence, NexusDefaultValues.NexusUrl)
        context.mapAttributeCheck(config, 'powershellModuleReadRepository', CharSequence, NexusDefaultValues.PowershellModuleRepository)

        context.println('----- getReadRepository done -----')

        return new PowershellModuleRepository(config.nexusUrl,config.powershellModuleReadRepository)
    }
}