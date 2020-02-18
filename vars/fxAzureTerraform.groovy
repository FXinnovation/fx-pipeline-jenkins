import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.di.IOC

def call(Map config = [:]) {

  def legacyFunction = {
    mapAttributeCheck(config, 'testPlanVars', List, [])
    mapAttributeCheck(config, 'publishPlanVars', List, [])

    registerServices()

    println("“fxAzureTerrafrom” is now replace by “fxAzureTerraformPipeline” for fx tests. For other tests/publish, please use “standardAzureTerraformPipeline”")
    fxTerraformWithUsernamePassword(
      testEnvironmentCredentialId: 'fxazure-terraformtests-service-principal',
      providerUsernameVariableName: 'client_id',
      providerPasswordVariableName: 'client_secret',
      initSSHCredentialId: 'gitea-fx_administrator-key',
      testPlanVars: [
        'subscription_id=7c6f67f4-4c27-42ea-bed3-1e4e988172ee',
        'tenant_id=4475f4e1-e2c3-42ec-9ffa-d341a5292a6a'
      ] + config.testPlanVars,
      publishPlanVars: [
        'subscription_id=7c6f67f4-4c27-42ea-bed3-1e4e988172ee',
        'tenant_id=4475f4e1-e2c3-42ec-9ffa-d341a5292a6a'
      ] + config.publishPlanVars,
      inspecTarget: 'azure',
      inspecSubscriptionId: '7c6f67f4-4c27-42ea-bed3-1e4e988172ee',
      inspecTenantId: '4475f4e1-e2c3-42ec-9ffa-d341a5292a6a'
    )
  }

  def deprecatedFunction = IOC.get(DeprecatedFunction.class.getName())
  deprecatedFunction.execute(legacyFunction, 'fxAzureTerrafrom', 'fxAzureTerraformPipeline', '12-05-2020')
}
