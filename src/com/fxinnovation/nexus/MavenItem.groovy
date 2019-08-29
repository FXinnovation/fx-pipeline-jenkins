package com.fxinnovation.nexus

class MavenItem implements Serializable  {

    public NexusRepository repository
    public String groupId
    public String version
    public String artefactId
    public String asset
    public String extension

    public MavenItem(
        NexusRepository repository,
        String groupId,
        String version,
        String artefactId,
        String asset,
        String extension
    ){
        this.repository = repository
        this.groupId = groupId
        this.version = version
        this.artefactId = artefactId
        this.asset = asset
        this.extension = extension
    }

    public String getUrl(){
        return "${this.repository.getBaseUrl()}/${this.groupId.replace(".", "/")}/${this.artefactId}/${this.version}/${this.asset}-${this.version}.${this.extension}"
    }

    public String getFileName(){
        return "${this.asset}-${this.version}.${this.extension}"
    }

    public Boolean testFileExist(){
        try{
            httpRequest httpMode: 'HEAD', ignoreSslErrors: true, responseHandle: 'NONE', url: this.getUrl(), validResponseCodes: '200'
            return true
        }
        catch(ex){            
            println(ex.toString());
            println(ex.getMessage());
            return false
        }
    }
    public String getFile(groovy.lang.Script context, String destinationFolder){

        context.execute(script: "mkdir -p ${destinationFolder}")

        def destinationFile = "${destinationFolder}/${getFileName()}"
        context.httpRequest(
            httpMode: 'GET', 
            ignoreSslErrors: true, 
            responseHandle: 'NONE', 
            url: this.getUrl(), 
            validResponseCodes: '200',
            outputFile: destinationFile
        )
        return destinationFile
    }

    public Boolean canPublish(){
        return true
    }

    def publish(groovy.lang.Script context, java.util.ArrayList files){
        if(canPublish()){
            context.printDebug("Will try to publish the artefact to ${getUrl()}")

            def assets = []
            for(filePath in files){
                assets += [
                                classifier: '', 
                                extension: '', 
                                filePath: filePath
                            ]
            }

            context.nexusPublisher(
                nexusInstanceId: this.repository.nexusUrl, 
                nexusRepositoryId: this.repository.repository, 
                packages: [
                    [
                        $class: 'MavenPackage', 
                        mavenAssetList: assets,
                        mavenCoordinate: [
                            artifactId: this.artefactId, 
                            groupId: this.groupId, 
                            packaging: this.extension, 
                            version: this.version
                        ]
                    ]
                ]
            )
        }
        else{
          println("Publish skipped because the file is already at '${getUrl()}''")
        }

    }
}