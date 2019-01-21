def call(Map config = [:]) {
  if (!config.containsKey('testEnvironmentCredentialId')) {
    error('“testEnvironmentCredentialId” parameter is mandatory.')
  }
  if (!config.containsKey('terraformCommandTarget')) {
    config.terraformCommandTarget = '.'
  }

  node {
    result="SUCCESS"
    try {
      ansiColor('xterm') {
        stage('checkout') {
          scmInfo = fx_checkout()
        }

        stage('init') {
          println terraform.init(
            commandTarget: config.terraformCommandTarget
          )
        }

        stage('validate'){
          println terraform.validate(
            commandTarget: config.terraformCommandTarget
          )
          println terraform.fmt(
            check: true,
            commandTarget: config.terraformCommandTarget
          )
        }

        withCredentials([
          usernamePassword(
            credentialsId: config.testEnvironmentCredentialId,
            usernameVariable: 'TF_access_key',
            passwordVariable: 'TF_secret_key'
          )
        ]) {
          try {
            println plan(
              TF_access_key,
              TF_secret_key,
              config.terraformCommandTarget
            )

            println terraform.apply(
              parallelism: 1,
              refresh: false,
              commandTarget: 'plan.out'
            )

            replay = plan(
              TF_access_key,
              TF_secret_key,
              config.terraformCommandTarget
            )

            println '###############################'
            println replay
            println '###############################'
            if ( !(replay =~ /.*Infrastructure is up-to-date.*/) ){
              error('Replaying the “apply” contains new changes. Make sure your terraform consecutive run makes no changes.')
            }
          } catch (errorApply) {
            archiveArtifacts(
              allowEmptyArchive: true,
              artifacts: 'terraform.tfstat*'
            )
            throw (errorApply)
          } finally {
            terraform.destroy(
              vars: [
                "access_key=${TF_access_key}",
                "secret_key=${TF_secret_key}"
              ],
              commandTarget: config.terraformCommandTarget
            )
          }
        }
      }
    }catch (error){
      result='FAILURE'
      throw (error)
    }finally {
      stage('notify'){
        fx_notify(
          status: result
        )
      }
    }
  }
}

/**
 *Terraform plan & apply
 * @param username
 * @param password
 * @param commandTarget
 * @return String terraform apply output
 */
def plan(String username, String password, String commandTarget){
  return terraform.plan(
    out: 'plan.out',
    vars: [
      "access_key=${username}",
      "secret_key=${password}"
    ],
    commandTarget: commandTarget
  )
}
