/****************************************
// This file should not be used anymore
// Obsolete
*****************************************/

package com.fxinnovation.nexus

class PowershellApplicationItem extends MavenItem{

    public PowershellApplicationItem(
        PowershellApplicationRepository repository,
        String client,
        String applicationName,
        String version,
        String asset
    ){
        super(
            repository,
            client,
            version,
            applicationName,
            asset,
            'zip')
    }

    public Boolean canPublish(){
        if(repository.isRelease){
            //Validate if publish is needed (with version only) [ If return != 200 we try to publish ]");
            return !this.testFileExist()
        }
        else{
            return true
        }
    }
}

