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
              credentialsId: 'itoa-application-awscollectors-awscred',
              passwordVariable: 'client_secret',
              usernameVariable: 'client_id'
            )
          ]
        ){
          def packerConfiguration = [
            commandTarget: 'configuration.pkr.hcl',
            vars: ["skip_create_ami=${!scmInfo.isPublishable()}"],
            dockerEnvironmentVariables: [
              AWS_ACCESS_KEY_ID:     client_id,
              AWS_SECRET_ACCESS_KEY: client_secret,
              PACKER_PLUGIN_PATH: "."
            ]
          ]
          pipelinePacker(
            [
              validateConfig: [packerConfiguration],
              buildConfig:    [packerConfiguration],
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
