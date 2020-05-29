def call(Map config = [:]) {
  mapAttributeCheck(config, 'validateVars', List, [])
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])

  registerServices()

  def subscriptionId = 'e469a261-e6fc-4363-94f1-3d8cdb259ec7'
  def tenantId = '219647b6-1ea6-409d-b9cc-0893cb535884'

  def fxAzureTerraformMandatoryVars = [
    "subscription_id=${subscriptionId}",
    "tenant_id=${tenantId}"
  ]

  standardAzureTerraformPipeline(
    testEnvironmentCredentialId: 'fxazure-terraformtests-12K-service-principal',
    initSSHCredentialId: 'gitea-fx_administrator-key',
    validateVars: config.validateVars,
    testPlanVars: fxAzureTerraformMandatoryVars + config.testPlanVars,
    publishPlanVars: fxAzureTerraformMandatoryVars + config.publishPlanVars,
    inspecSubscriptionId: subscriptionId,
    inspecTenantId: tenantId
  )
}
