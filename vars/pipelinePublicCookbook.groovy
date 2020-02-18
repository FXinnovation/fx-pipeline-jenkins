import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  closureHelper = new ClosureHelper(this, closures)

  if(!closureHelper.isDefined('publish')) {
    mapAttributeCheck(config, 'supermarketCredentialsId', CharSequence, '', '“supermarketCredentialsId” is mandatory')

    closureHelper.addClosure('publish', {
        stove(
          credentialsId: config.supermarketCredentialsId
        )
      }
    )
  }
  pipelineCookbook(
    config,
    closureHelper.getCLosures()
  )
}
