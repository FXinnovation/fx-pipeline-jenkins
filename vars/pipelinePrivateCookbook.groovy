import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  closureHelper = new ClosureHelper(this, closures)

  if (!closureHelper.isDefined('publish')){
    mapAttributeCheck(config, 'credentialId', CharSequence, '', '“credentialId” is mandatory')
    mapAttributeCheck(config, 'serverUrl', CharSequence, '', '“serverUrl” is mandatory')
    mapAttributeCheck(config, 'cookbookName', CharSequence, '', '“cookbookName” is mandatory')
    mapAttributeCheck(config, 'cookbookPath', CharSequence, '', '“cookbookPath” is mandatory')

    closureHelper.addClosure('publish', {
        cookbookUploadOutput = knife.cookbookUpload([
          credentialId: config.credentialId,
          serverUrl: config.serverUrl,
          commandTarget: config.cookbookName,
          cookbookPath: config.cookbookPath,
         ]
        ) 
        
        if (cookbookUploadOutput.stderr =~ /ERROR: Could not find cookbook/) {
          error(cookbookUploadOutput.stderr)
        } 
      }
    )
  }
  if (!config.containsKey('foodcritic')){
    config.foodcritic = [
      options: '-t \'~FC078\''
    ]
  }
  pipelineCookbook(
    config,
    closureHelper.getClosures()
  )
}
