def call(Map config = [:]) {
  mapAttributeCheck(config, 'validateVars', List, [])
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])

  def subscriptionId = '7c6f67f4-4c27-42ea-bed3-1e4e988172ee'
  def tenantId = '4475f4e1-e2c3-42ec-9ffa-d341a5292a6a'

  def fxAzureTerraformMandatoryVars = [
    "subscription_id=${subscriptionId}",
    "tenant_id=${tenantId}"
  ]

  standardAzureTerraformPipeline(
    testEnvironmentCredentialId: 'fxazure-terraformtests-service-principal',
    initSSHCredentialId: 'gitea-fx_administrator-key',
    validateVars: config.validateVars,
    testPlanVars: fxAzureTerraformMandatoryVars + config.testPlanVars,
    publishPlanVars: fxAzureTerraformMandatoryVars + config.publishPlanVars,
    inspecSubscriptionId: subscriptionId,
    inspecTenantId: tenantId
  )
}
