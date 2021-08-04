import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]) {
  registerServices()

  mapAttributeCheck(config, 'testEnvironmentCredentialId', CharSequence, '', '“testEnvironmentCredentialId” parameter is mandatory.')
  mapAttributeCheck(config, 'publishEnvironmentCredentialId', CharSequence, config.testEnvironmentCredentialId)
  mapAttributeCheck(config, 'providerUsernameVariableName', CharSequence, 'access_key')
  mapAttributeCheck(config, 'providerPasswordVariableName', CharSequence, 'secret_key')
  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])

  closureHelper = new ClosureHelper(this, closures)

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

    config.publishPlanVars = [
      "${config.providerUsernameVariableName}=${TF_username_publish}",
      "${config.providerPasswordVariableName}=${TF_password_publish}",
    ] + config.publishPlanVars

    config.inspecUsername = TF_username_test
    config.inspecPassword = TF_password_test
   println config.slaveSize
   fxTerraform(config, closureHelper.getClosures())
  }
}
