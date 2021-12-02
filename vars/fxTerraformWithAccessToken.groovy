import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', '"testEnvironmentCredentialId" parameter is mandatory.')
  mapAttributeCheck(config, 'publishEnvironmentCredentialId', CharSequence, config.testEnvironmentCredentialId)
  mapAttributeCheck(config, 'providerOwnerVariableName', CharSequence, 'owner')
  mapAttributeCheck(config, 'providerAccessTokenVariableName', CharSequence, 'token')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])

  closureHelper = new ClosureHelper(this, closures)

  withCredentials([
    usernamePassword(
      credentialsId: config.testEnvironmentCredentialId,
      usernameVariable: 'TF_owner_test',
      passwordVariable: 'TF_token_test'
    ),
    usernamePassword(
      credentialsId: config.publishEnvironmentCredentialId,
      usernameVariable: 'TF_owner_publish',
      passwordVariable: 'TF_token_publish'
    ),
  ]) {
    config.testPlanVars = [
      "${config.providerOwnerVariableName}=${TF_owner_test}",
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
    ] + config.testPlanVars

    config.validateVars = [
      "${config.providerOwnerVariableName}=${TF_owner_test}",
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
    ] + config.validateVars

    config.publishPlanVars = [
      "${config.providerOwnerVariableName}=${TF_owner_publish}",
      "${config.providerAccessTokenVariableName}=${TF_token_publish}",
    ] + config.publishPlanVars

    config.inspecPassword = TF_token_test

   fxTerraform(config, closureHelper.getClosures())
  }
}
