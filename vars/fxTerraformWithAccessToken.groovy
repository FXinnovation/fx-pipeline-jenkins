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
      usernameVariable: 'TF_user_test',
      passwordVariable: 'TF_token_test'
    ),
    usernamePassword(
      credentialsId: config.publishEnvironmentCredentialId,
      usernameVariable: 'TF_user_publish',
      passwordVariable: 'TF_token_publish'
    ),
  ]) {
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
