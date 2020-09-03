import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]) {
  registerServices()

  mapAttributeCheck(config, 'awsNukeConfigFileName', CharSequence, '', 'ERROR: “awsNukeConfigFileName” must be defined!')
  mapAttributeCheck(config, 'credentialsId', CharSequence, '', 'ERROR: “credentialsId” must be defined!')
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/aws-nuke:latest')
  mapAttributeCheck(config, 'region', CharSequence, 'ca-central-1')
  mapAttributeCheck(config, 'tokenDuration', Integer, 3600)
  mapAttributeCheck(config, 'commonOptions', Map, [:])
  mapAttributeCheck(config, 'recreateDefaultVpcResources', Boolean, true)

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.addClosure('preAwsNuke', { Map data ->
    withAWS(
      credentials: config.credentialsId, 
      duration: config.tokenDuration, 
      region: config.region)
    {
       def accessKey = execute(
           hideStdout: true,
           script: 'echo $AWS_ACCESS_KEY_ID'
       )
       
       def secretKey = execute(
           hideStdout: true,
           script: 'echo $AWS_SECRET_ACCESS_KEY'
       ) 
       
       def defaultRegion = execute(
           hideStdout: true,
           script: 'echo $AWS_DEFAULT_REGION'
       )

       data.put("accessKeyId", accessKey.stdout)
       data.put("secretAccessKey", secretKey.stdout)
       data.put("defaultRegion", defaultRegion.stdout)

       return data
    }
  })

  pipelineAwsNuke([
      dockerImage: config.dockerImage,
      awsNukeConfigFileName: config.awsNukeConfigFileName,
      recreateDefaultVpcResources: config.recreateDefaultVpcResources,
    ] + config.commonOptions,
    closureHelper.getClosures()
  )
}
