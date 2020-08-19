import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.io.Debugger

def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/aws-nuke:latest')
  mapAttributeCheck(config, 'dockerAdditionalMounts', Map, [:])
  mapAttributeCheck(config, 'dockerEnvironmentVariables', Map, [:])
  mapAttributeCheck(config, 'dockerNetwork', CharSequence, 'bridge')
  mapAttributeCheck(config, 'config', CharSequence, '', 'ERROR: “config” is mandatory!')
  mapAttributeCheck(config, 'throwError', Boolean, true)
  

  def debugger = new Debugger(this)

  def optionStringFactory = new OptionStringFactory(this)
  optionStringFactory.createOptionString(' ')

  optionStringFactory.addOption('--force')

  if(config.containsKey('config') && config.config){
    optionStringFactory.addOption('--config', config.config, CharSequence)
  }
  if(config.containsKey('exclude') && config.exclude){
    optionStringFactory.addOption('--exclude', config.exclude, CharSequence)
  }
  if(config.containsKey('forceSleep') && config.forceSleep){
    optionStringFactory.addOption('--force-sleep', config.forceSleep, Integer)
  }
  if(config.containsKey('maxWaitRetries') && config.maxWaitRetries){
    optionStringFactory.addOption('--max-wait-retries', config.maxWaitRetries, Integer)
  }
  if(config.containsKey('noDryRun') && config.noDryRun){
    optionStringFactory.addOption('--no-dry-run')
  }
  if(config.containsKey('quiet') && config.quiet){
    optionStringFactory.addOption('--quiet')
  }

  if(config.containsKey('recreateDefaultVpcResources') && config.recreateDefaultVpcResources){
    config.dockerEnvironmentVariables.put('RECREATE_DEFAULT_VPC_RESOURCES', 'true')
  }
  
  if(config.containsKey('accessKeyId') && config.accessKeyId){
    config.dockerEnvironmentVariables.put('AWS_ACCESS_KEY_ID', config.accessKeyId)
  }
  
  if(config.containsKey('secretAccessKey') && config.secretAccessKey){
    config.dockerEnvironmentVariables.put('AWS_SECRET_ACCESS_KEY', config.secretAccessKey)
  }
  
  if(config.containsKey('sessionToken') && config.sessionToken){
    config.dockerEnvironmentVariables.put('AWS_SESSION_TOKEN', config.sessionToken)
  }
  
  if(config.containsKey('defaultRegion') && config.defaultRegion){
    config.dockerEnvironmentVariables.put('AWS_DEFAULT_REGION', config.defaultRegion)
  }
  
  if(config.containsKey('profile') && config.profile){
    config.dockerEnvironmentVariables.put('AWS_PROFILE', config.profile)
  }
  

  awsNukeCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand:  'aws-nuke',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables,
    network: config.dockerNetwork,
  )

  if(debugger.debugVarExists()) {
    execute(
      script: "${awsNukeCommand} version"
    )
    optionStringFactory.addOption('--verbose')
  }

  return execute(
    throwError: config.throwError,
    script: "${awsNukeCommand} ${optionStringFactory.getOptionString().toString()}"
  )
}
