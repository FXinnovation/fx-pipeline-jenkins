package com.fxinnovation.nexus

class NexusRepository implements Serializable{
    public String repository
    public String nexusUrl

    public NexusRepository(        
        String nexusUrl,
        String repository
    ){
        this.repository = repository
        this.nexusUrl = nexusUrl
    }

    public String getBaseUrl(){
        return "${this.nexusUrl}/repository/${this.repository}"
    }
}