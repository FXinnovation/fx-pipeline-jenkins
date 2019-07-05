def call(Map config = [:]) {
  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', '“testEnvironmentCredentialId” parameter is mandatory.')
  mapAttributeCheck(config, 'publishEnvironmentCredentialId', CharSequence, config.testEnvironmentCredentialId)
  mapAttributeCheck(config, 'providerSecretFileVariableName', CharSequence, 'credentials')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])

  withCredentials([
    file(
      credentialsId: config.testEnvironmentCredentialId,
      variable: 'accountCredentials')
  ]) {
    println 'DEBUG1'
    // This can be unsafe, SL-577 in Jira should change this.
    execute(
      script: "cp ${accountCredentials} ./account.json"
    )
    println 'DEBUG2'
    config.testPlanVars = [
      "${config.providerSecretFileVariableName}='./account.json'"
    ] + config.testPlanVars

    config.validateVars = [
      "${config.providerSecretFileVariableName}='./account.json'"
    ] + config.validateVars

    config.publishPlanVars = [
      "${config.providerSecretFileVariableName}='./account.json'"
    ] + config.publishPlanVars

    fxTerraform(config)
  }
}
