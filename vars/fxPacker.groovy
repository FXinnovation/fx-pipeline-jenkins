import com.fxinnovation.data.ScmInfo

def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerRegistryLogin', Boolean, false)

  registerListeners()
  standardJob([
    pipeline: {
      ScmInfo scmInfo ->
        withCredentials(
          [
            usernamePassword(
              credentialsId: 'jenkins-test-account',
              passwordVariable: 'client_secret',
              usernameVariable: 'client_id'
            )
          ]
        ){
          def packerConfiguration = [
            commandTarget: 'configuration.json',
            dockerEnvironmentVariables: [
              AWS_ACCESS_KEY_ID:     client_id,
              AWS_SECRET_ACCESS_KEY: client_secret
            ]
          ]
          pipelinePacker(
            [
              validateConfig: [packerConfiguration],
              buildConfig:    [packerConfiguration],
              publish:        true
            ]
          )
        }
    },
    notification: {
      return "Nothing to do!!"
    }
  ],
  [],
  config
  )
}