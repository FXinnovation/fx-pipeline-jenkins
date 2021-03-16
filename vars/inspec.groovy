import com.fxinnovation.di.IOC
import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.helper.DockerRunnerHelper

def exec(Map config = [:]){
  config.subCommand = 'exec'
    validParameters = [
    'target': '',
    'jsonConfig': '',
    'dockerImage':'',
    'subCommand':'',
    'reporter':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("inspec - Parameter \"${parameter.key}\" is not valid for \"exec\", please remove it!")
    }
  }
  inspec(config)
}

def call(Map config = [:]){
  mapAttributeCheck(config, 'commandTarget', CharSequence, '', 'commandTarget parameter is mandatory')
  mapAttributeCheck(config, 'dockerAdditionalMounts', Map, [:])
  mapAttributeCheck(config, 'dockerEnvironmentVariables', Map, [:])
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/inspec:latest')
  mapAttributeCheck(config, 'subCommand', CharSequence, '', 'subCommand parameter is mandatory')

  def dockerRunnerHelper = IOC.get(DockerRunnerHelper.class.getName())
  def debugger = IOC.get(Debugger.class.getName())

  if (debugger.debugVarExists()) {
    dockerRunnerHelper.prepareRunCommand(
      config.dockerImage,
      'inspec',
      '--version',
    )
    dockerRunnerHelper.run()
  }

  dockerRunnerHelper.prepareRunCommand(
    config.dockerImage,
    'inspec',
    this.getInspecSubCommand(config),
    config.dockerAdditionalMounts,
    config.dockerEnvironmentVariables
  )

  dockerRunnerHelper.run()
}

private String getInspecSubCommand(Map config = [:]) {
  def optionStringFactory = new OptionStringFactory(this)
  optionStringFactory.createOptionString('=')

  if (config.containsKey('target') && config.target instanceof CharSequence){
    optionStringFactory.addOption('--target', config.target)
  }
  if (config.containsKey('jsonConfig') && config.jsonConfig instanceof CharSequence){
    optionStringFactory.addOption('--json-config', config.jsonConfig)
  }

  optionStringFactory.addOption(config.commandTarget)

  if (config.containsKey('reporter') && config.reporter instanceof CharSequence){
    optionStringFactory.addOption('--reporter', config.reporter)
  }
  optionStringFactory.addOption('--chef-license', 'accept-silent')
  optionStringFactory.addOption('--no-distinct-exit')

  return "${config.subCommand} ${optionStringFactory.getOptionString().toString()}"
}