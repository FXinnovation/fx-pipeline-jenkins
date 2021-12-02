import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', '"testEnvironmentCredentialId" parameter is mandatory.')
  mapAttributeCheck(config, 'publishEnvironmentCredentialId', CharSequence, config.testEnvironmentCredentialId)
  mapAttributeCheck(config, 'providerAccessTokenVariableName', CharSequence, 'access_token')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])

  closureHelper = new ClosureHelper(this, closures)

  withCredentials(
    [string(credentialsId: config.testEnvironmentCredentialId, variable: 'TF_token_test')],
    [string(credentialsId: config.publishEnvironmentCredentialId, variable: 'TF_token_publish')],
  ) {
    config.testPlanVars = [
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
    ] + config.testPlanVars

    config.validateVars = [
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
    ] + config.validateVars

    config.publishPlanVars = [
      "${config.providerAccessTokenVariableName}=${TF_token_publish}",
    ] + config.publishPlanVars

    config.inspecPassword = TF_token_test

   fxTerraform(config, closureHelper.getClosures())
  }
}
