def call(Map config = [:]) {
  if (!config.containsKey('testEnvironmentCredentialId') || !(config.testEnvironmentCredentialId instanceof CharSequence)) {
    error('“testEnvironmentCredentialId” parameter is mandatory.')
  }
  if (!config.containsKey('providerUsernameVariableName') || !(config.providerUsernameVariableName instanceof CharSequence)) {
    config.providerUsernameVariableName = 'access_key'
  }
  if (!config.containsKey('providerPasswordVariableName') || !(config.providerPasswordVariableName instanceof CharSequence)) {
    config.providerPasswordVariableName = 'secret_key'
  }

  withCredentials([
    usernamePassword(
      credentialsId: config.testEnvironmentCredentialId,
      usernameVariable: 'TF_username',
      passwordVariable: 'TF_password'
    )
  ]) {
    fxTerraform(
      [
        planVars: [
          "${config.providerUsernameVariableName}=${TF_username}",
          "${config.providerPasswordVariableName}=${TF_password}",
        ]
      ] + config
    )
  }
}
