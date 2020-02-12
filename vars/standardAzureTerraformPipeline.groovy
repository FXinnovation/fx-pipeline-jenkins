
def call(Map config = [:]) {
  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', "“testEnvironmentCredentialId” is mandatory")
  mapAttributeCheck(config, 'publishEnvironmentCredentialId', CharSequence, config.testEnvironmentCredentialId)
  mapAttributeCheck(config, 'initSSHCredentialId', CharSequence, '', "“initSSHCredentialId” is mandatory")
  mapAttributeCheck(config, 'validateVars', List, [])
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'inspecSubscriptionId', CharSequence, '')
  mapAttributeCheck(config, 'inspecTenantId', CharSequence, '')

  fxTerraformWithUsernamePassword(
    testEnvironmentCredentialId: config.testEnvironmentCredentialId,
    publishEnvironmentCredentialId: config.publishEnvironmentCredentialId,
    providerUsernameVariableName: 'client_id',
    providerPasswordVariableName: 'client_secret',
    initSSHCredentialId: config.initSSHCredentialId,
    validateVars: config.validateVars,
    testPlanVars: config.testPlanVars,
    publishPlanVars: config.publishPlanVars,
    inspecTarget: 'azure',
    inspecSubscriptionId: config.inspecSubscriptionId,
    inspecTenantId: config.inspecTenantId
  )
}
