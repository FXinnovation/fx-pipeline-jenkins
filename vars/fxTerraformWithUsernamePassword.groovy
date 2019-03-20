def call(Map config = [:]) {
  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', '“testEnvironmentCredentialId” parameter is mandatory.')
  mapAttributeCheck(config, 'providerUsernameVariableName', CharSequence, 'access_key')
  mapAttributeCheck(config, 'providerPasswordVariableName', CharSequence, 'secret_key')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])

  withCredentials([
    usernamePassword(
      credentialsId: config.testEnvironmentCredentialId,
      usernameVariable: 'TF_username_test',
      passwordVariable: 'TF_password_test'
    )
  ]) {
    config.testPlanVars = [
      "${config.providerUsernameVariableName}=${TF_username_test}",
      "${config.providerPasswordVariableName}=${TF_password_test}",
    ] + config.testPlanVars

    config.validateVars = [
      "${config.providerUsernameVariableName}=${TF_username_test}",
      "${config.providerPasswordVariableName}=${TF_password_test}",
    ] + config.publishPlanVars

    fxTerraform(config)
  }
}
