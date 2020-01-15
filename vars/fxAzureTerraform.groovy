def call(Map config = [:]) {
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])

  fxTerraformWithUsernamePassword(
    testEnvironmentCredentialId: 'fxprometheus-service-principal',
    providerUsernameVariableName: 'client_id',
    providerPasswordVariableName: 'client_secret',
    initSSHCredentialId: 'gitea-fx_administrator-key',
    testPlanVars: [
      'subscription_id=9ea1187f-441c-43f4-af71-8f54123f2ed1',
      'tenant_id=c8be77fb-3cf8-4d5a-b446-a3c65e7ae3db'
    ] + config.testPlanVars,
    publishPlanVars: [
      'subscription_id=9ea1187f-441c-43f4-af71-8f54123f2ed1',
      'tenant_id=c8be77fb-3cf8-4d5a-b446-a3c65e7ae3db'
    ] + config.publishPlanVars,
    inspecTarget: 'azure',
    inspecSubscriptionId: '9ea1187f-441c-43f4-af71-8f54123f2ed1',
    inspecTenantId: 'c8be77fb-3cf8-4d5a-b446-a3c65e7ae3db'
  )
}
