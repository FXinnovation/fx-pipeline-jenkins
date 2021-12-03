import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', '"testEnvironmentCredentialId" parameter is mandatory.')
  mapAttributeCheck(config, 'publishEnvironmentCredentialId', CharSequence, config.testEnvironmentCredentialId)
  mapAttributeCheck(config, 'testEnvironmentTokenId', CharSequence, '', '"testEnvironmentTokenId" parameter is mandatory.')
  mapAttributeCheck(config, 'publishEnvironmentTokenId', CharSequence, config.testEnvironmentTokenId)
  mapAttributeCheck(config, 'providerOwnerVariableName', CharSequence, 'owner')
  mapAttributeCheck(config, 'providerAccessTokenVariableName', CharSequence, 'token')
  mapAttributeCheck(config, 'providerUsernameVariableName', CharSequence, 'access_key')
  mapAttributeCheck(config, 'providerPasswordVariableName', CharSequence, 'secret_key')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])

  closureHelper = new ClosureHelper(this, closures)

  withCredentials([
    usernamePassword(
      credentialsId: config.testEnvironmentCredentialId,
      usernameVariable: 'TF_username_test',
      passwordVariable: 'TF_password_test'
    ),
    usernamePassword(
      credentialsId: config.publishEnvironmentCredentialId,
      usernameVariable: 'TF_username_publish',
      passwordVariable: 'TF_password_publish'
    ),
    usernamePassword(
      credentialsId: config.testEnvironmentTokenId,
      usernameVariable: 'TF_owner_test',
      passwordVariable: 'TF_token_test'
    ),
    usernamePassword(
      credentialsId: config.publishEnvironmentTokenId,
      usernameVariable: 'TF_owner_publish',
      passwordVariable: 'TF_token_publish'
    ),
  ]) {
    config.testPlanVars = [
      "${config.providerUsernameVariableName}=${TF_username_test}",
      "${config.providerPasswordVariableName}=${TF_password_test}",
      "${config.providerOwnerVariableName}=FXinnovation",
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
    ] + config.testPlanVars

    config.validateVars = [
      "${config.providerOwnerVariableName}=FXinnovation",
      "${config.providerAccessTokenVariableName}=${TF_token_test}",
      "${config.providerUsernameVariableName}=${TF_username_test}",
      "${config.providerPasswordVariableName}=${TF_password_test}",
    ] + config.validateVars

    config.publishPlanVars = [
      "${config.providerOwnerVariableName}=FXinnovation",
      "${config.providerAccessTokenVariableName}=${TF_token_publish}",
      "${config.providerUsernameVariableName}=${TF_username_publish}",
      "${config.providerPasswordVariableName}=${TF_password_publish}",
    ] + config.publishPlanVars

    config.inspecUsername = TF_username_test
    config.inspecPassword = TF_password_test

   fxTerraform(config, closureHelper.getClosures())
  }
}
