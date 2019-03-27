def call(Map config = [:]) {
  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', '“testEnvironmentCredentialId” parameter is mandatory.')
  mapAttributeCheck(config, 'publishEnvironmentCredentialId', CharSequence, '', '“publishEnvironmentCredentialId” parameter is mandatory.')
  mapAttributeCheck(config, 'providerUsernameVariableName', CharSequence, 'access_key')
  mapAttributeCheck(config, 'providerPasswordVariableName', CharSequence, 'secret_key')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])

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
    )
  ]) {
    config.testPlanVars = [
      "${config.providerUsernameVariableName}=${TF_username_test}",
      "${config.providerPasswordVariableName}=${TF_password_test}",
    ] + config.testPlanVars

    config.validateVars = [
      "${config.providerUsernameVariableName}=${TF_username_test}",
      "${config.providerPasswordVariableName}=${TF_password_test}",
    ] + config.validateVars

    config.publishPlanVars = [
      "${config.providerUsernameVariableName}=${TF_username_publish}",
      "${config.providerPasswordVariableName}=${TF_password_publish}",
    ] + config.publishPlanVars

    fxTerraform(config)
  }
}
