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
      "GITHUB_OWNER=FXinnovation",
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
    ] + config.testPlanVars

    config.validateVars = [
      "GITHUB_OWNER=FXinnovation",
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
    ] + config.validateVars

    config.publishPlanVars = [
      "GITHUB_OWNER=FXinnovation",
      "${config.providerAccessTokenVariableName}=${TF_token_publish}",
    ] + config.publishPlanVars

    config.inspecPassword = TF_token_test

   fxTerraform(config, closureHelper.getClosures())
  }
}
