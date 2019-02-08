def call(Map config = [:]) {
  if (!config.containsKey('testEnvironmentCredentialId') || !(config.providerAccessKeyVariableName instanceof CharSequence)) {
    error('“testEnvironmentCredentialId” parameter is mandatory.')
  }
  if (!config.containsKey('providerAccessKeyVariableName') || !(config.providerAccessKeyVariableName instanceof CharSequence)) {
    config.providerAccessKeyVariableName = 'access_key'
  }
  if (!config.containsKey('providerSecretKeyVariableName') || !(config.providerAccessKeyVariableName instanceof CharSequence)) {
    config.providerAccessKeyVariableName = 'secret_key'
  }

  withCredentials([
    usernamePassword(
      credentialsId: config.testEnvironmentCredentialId,
      usernameVariable: 'TF_test_access_key',
      passwordVariable: 'TF_test_secret_key'
    )
  ]) {
    fxTerraform([
      planVars: [
        "${config.providerAccessKeyVariableName}=${TF_test_access_key}",
        "${config.providerSecretKeyVariableName}=${TF_test_secret_key}",
      ]
    ] + config
    )
  }
}
