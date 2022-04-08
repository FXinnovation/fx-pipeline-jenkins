import com.fxinnovation.di.IOC
import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.helper.DockerRunnerHelper
import com.fxinnovation.io.Debugger

Map getShowValidParameters() {
  return [
    'noColor': [
      type: Boolean,
      default: 'false',
      description: 'If specified, output won\'t contain any color.',
    ],
    'moduleDepth': [
      type: Integer,
      default: -1,
      description: 'Specifies the depth of modules to show in the output. By default this is -1, which will expand all.',
    ],
  ]
}

def show(Map config = [:]){
  config.subCommand = 'show -no-color'
  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!(getShowValidParameters().containsKey(key) || getCommonValidParameters().containsKey(key))) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
      println("Configuration: ${config}")
    }
  }

  // NOTE: This has been commented out becasue of https://github.com/hashicorp/terraform/issues/23377
  // it can be activated again when the bug is resolved. (not too long I hope)
  // terraform(config)
}

def output(Map config = [:]){
  config.subCommand = 'output -no-color'
  validParameters = [
    'noColor':'',
    'json':'',
    'module':'',
    'state': '',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
  ]
  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!validParameters.containsKey(key)) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
      println("Configuration: ${config}")
    }
  }

  println("Configuration: ${config}")
  terraform(config)
}

def validate(Map config = [:]){
  config.subCommand = 'validate -no-color'
  validParameters = [
    'checkVariables':'',
    'noColor':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
    'terraformVersion1':'',
  ]
  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!validParameters.containsKey(key)) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
      println("Configuration: ${config}")
    }
  }

  println("Configuration: ${config}")
  terraform(config)
}


Map getStateMvValidParameters() {
  return [
    'backup': [
      type: CharSequence,
      default: '',
      description: 'Path to backup the existing state file before modifying. Defaults to the "-state-out" path with .backup" extension. Set to "-" to disable backup.',
    ],
    'backupOut': [
      type: CharSequence,
      default: '',
      description: 'Path where Terraform should write the backup for the destination state. This can\'t be disabled. If not set, Terraform will write it to the same path as the destination state file with a backup extension. This only needs to be specified if -state-out is set to a different path than -state.',
    ],
    'lock': [
      type: Boolean,
      default: true,
      description: 'Lock the state file when locking is supported.',
    ],
    'lockTimeout': [
      type: CharSequence,
      default: '0s',
      description: 'Duration to retry a state lock.',
    ],
    'state': [
      type: CharSequence,
      default: 'terraform.tfstate',
      description: 'Path to read and save state (unless state-out is specified).',
    ],
    'stateOut': [
      type: CharSequence,
      default: '',
      description: 'Path to write state to that is different than "-state". This can be used to preserve the old state.',
    ],
  ]
}

def stateMv(Map config = [:]){
  config.subCommand = 'state mv'
  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!(getStateValidParameters().containsKey(key) || getCommonValidParameters().containsKey(key))) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
      println("Configuration: ${config}")
    }
  }

  println("Configuration: ${config}")
  terraform(config)
}

Map getRefreshValidParameters() {
  return [
    'backup': [
      type: CharSequence,
      default: '',
      description: 'Path to backup the existing state file before modifying. Defaults to the "-state-out" path with .backup" extension. Set to "-" to disable backup.',
    ],
    'lock': [
      type: Boolean,
      default: true,
      description: 'Lock the state file when locking is supported.',
    ],
    'lockTimeout': [
      type: CharSequence,
      default: '0s',
      description: 'Duration to retry a state lock.',
    ],
    'noColor': [
      type: Boolean,
      default: 'false',
      description: 'If specified, output won\'t contain any color.',
    ],
    'parallelism': [
      type: Integer,
      default: 10,
      description: 'Limit the number of concurrent operations.',
    ],
    'state': [
      type: CharSequence,
      default: 'terraform.tfstate',
      description: 'Path to read and save state (unless state-out is specified).',
    ],
    'stateOut': [
      type: CharSequence,
      default: '',
      description: 'Path to write state to that is different than "-state". This can be used to preserve the old state.',
    ],
    'targets':  [
      type: ArrayList,
      default: '',
      description: 'Resource to target. Operation will be limited to this resource and its dependencies. This flag can be used multiple times',
    ],
    'vars':  [
      type: ArrayList,
      default: '',
      description: 'Set a variable in the Terraform configuration. This flag can be set multiple times',
    ],
    'varFile': [
      type: CharSequence,
      default: '',
      description: 'Set variables in the Terraform configuration from a file. If "terraform.tfvars" or any ".auto.tfvars" files are present, they will be automatically loaded.',
    ]
  ]
}

def refresh(Map config = [:]){
  config.subCommand = 'refresh -no-color'
  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!(getRefreshValidParameters().containsKey(key) || getCommonValidParameters().containsKey(key))) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
      println("Configuration: ${config}")
    }
  }

  println("Configuration: ${config}")
  terraform(config)
}

def slowRefresh(Map config = [:]){
  config.subCommand = 'refresh -no-color'
  validParameters = [
    'backup':'',
    'lock':'',
    'lockTimeout':'',
    'noColor':'',
    'state':'',
    'stateOut':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
  ]

  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!validParameters.containsKey(key)){
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
    }
  }

  terraformFiles = findFiles(glob: '*.tf')
  config.targets = []
  for ( terraformFile in terraformFiles ) {
    currentResources = readJSON text: sh(
      returnStdout: true,
      script: "cat ${terraformFile.toString()} | docker run --rm -i fxinnovation/json2hcl -reverse"
    )
    for ( resource in currentResources.resource ){
      currentResourceType = resource.keySet().toArray()[0]
      for ( tfResource in resource."${currentResourceType}") {
        currentResourceId = tfResource.keySet().toArray()[0]
        config.targets[config.targets.size()] = "'${currentResourceType}.${currentResourceId}'"
        if ( config.targets.size() >= 5 ){
          terraform(config)
          config.targets = []
          sh 'sleep 1'
        }
      }
    }

    println("Call terraform with config: ${config}")
    terraform(config)
  }
}

Map getInitValidParameters() {
  return [
    'backend': [
      type: Boolean,
      default: true,
      description: 'Configure the backend for this configuration.',
    ],
    'backendConfigs': [
      type: ArrayList,
      default: '',
      description: 'This can be either a path to an HCL file with key/value assignments (same format as terraform.tfvars) or a \'key=value\' format. This is merged with what is in the configuration file. This can be specified multiple times. The backend type must be in the configuration itself.',
    ],
    'forceCopy': [
      type: Boolean,
      default: false,
      description: 'Suppress prompts about copying state data. This is equivalent to providing a "yes" to all confirmation prompts.',
    ],
    'fromModule': [
      type: CharSequence,
      default: '',
      description: 'Copy the contents of the given module into the target directory before initialization.',
    ],
    'get': [
      type: Boolean,
      default: true,
      description: 'Download any modules for this configuration.',
    ],
    'input': [
      type: Boolean,
      default: true,
      description: 'Ask for input if necessary. If false, will error if input was required.',
    ],
    'lock': [
      type: Boolean,
      default: true,
      description: 'Lock the state file when locking is supported.',
    ],
    'lockTimeout': [
      type: CharSequence,
      default: '0s',
      description: 'Duration to retry a state lock.',
    ],
    'noColor': [
      type: Boolean,
      default: 'false',
      description: 'If specified, output won\'t contain any color.',
    ],
    'pluginDirs': [
      type: CharSequence,
      default: '',
      description: 'Directory containing plugin binaries. This overrides all default search paths for plugins, and prevents the automatic installation of plugins. This flag can be used multiple times.',
    ],
    'reconfigure': [
      type: Boolean,
      default: false,
      description: 'Reconfigure the backend, ignoring any saved configuration.',
    ],
    'upgrade': [
      type: Boolean,
      default: false,
      description: 'If installing modules (-get) or plugins (-get-plugins), ignore previously-downloaded objects and install the latest version allowed within configured constraints.',
    ],
    'verifyPlugins': [
      type: Boolean,
      default: true,
      description: 'Verify the authenticity and integrity of automatically downloaded plugins.',
    ],
    'terraformVersion1': [
      type: Boolean,
      default: false,
      description: 'Indicate if we are running a version of terraform >= 1.',
    ],
  ]
}

def init(Map config = [:]){
  config.subCommand = 'init -no-color'
  for ( parameter in config ) {
    if ( !(getInitValidParameters().containsKey(parameter.key) || getCommonValidParameters().containsKey(parameter.key))){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def plan(Map config = [:]){
  config.subCommand = 'plan -no-color'
  validParameters = [
    'destroy':'',
    'lock':'',
    'lockTimeout':'',
    'moduleDepth':'',
    'noColor':'',
    'out':'',
    'parallelism':'',
    'refresh':'',
    'state':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
    'terraformVersion1':'',
  ]

  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!(validParameters.containsKey(key) || getCommonValidParameters().containsKey(key))) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
      println("Configuration: ${config}")
    }
  }

  config.input=false
  println("Configuration: ${config}")
  terraform(config)
}

def apply(Map config = [:]){
  mapAttributeCheck(config, 'terraformVersion1', Boolean, false)

  config.subCommand = 'apply -no-color'
  validParameters = [
    'backup':'',
    'lock':'',
    'lockTimeout':'',
    'noColor':'',
    'parallelism':'',
    'refresh':'',
    'state':'',
    'stateOut':'',
    'targets':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
    'terraformVersion1':'',
    'planFile':'',
  ]
  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!(validParameters.containsKey(key) || getCommonValidParameters().containsKey(key))) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
      println("Configuration: ${config}")
    }
  }

  config.autoApprove=true
  config.input=false
  println("Configuration: ${config}")
  if ( !config.terraformVersion1.toBoolean() ) { config.commandTarget = config.planFile }
  terraform(config)
}

Map getDestroyValidParameters() {
  return [
    'backup': [
      type: CharSequence,
      default: '',
      description: 'Path to backup the existing state file before modifying. Defaults to the "-state-out" path with .backup" extension. Set to "-" to disable backup.',
    ],
    'lock': [
      type: Boolean,
      default: true,
      description: 'Lock the state file when locking is supported.',
    ],
    'lockTimeout': [
      type: CharSequence,
      default: '0s',
      description: 'Duration to retry a state lock.',
    ],
    'noColor': [
      type: Boolean,
      default: 'false',
      description: 'If specified, output won\'t contain any color.',
    ],
    'parallelism': [
      type: Integer,
      default: 10,
      description: 'Limit the number of concurrent operations.',
    ],
    'refresh': [
      type: Boolean,
      default: true,
      description: 'Update state prior to checking for differences. This has no effect if a plan file is given to apply.',
    ],
    'state': [
      type: CharSequence,
      default: 'terraform.tfstate',
      description: 'Path to read and save state (unless state-out is specified).',
    ],
    'stateOut': [
      type: CharSequence,
      default: '',
      description: 'Path to write state to that is different than "-state". This can be used to preserve the old state.',
    ],
    'targets':  [
      type: ArrayList,
      default: '',
      description: 'Resource to target. Operation will be limited to this resource and its dependencies. This flag can be used multiple times',
    ],
    'vars':  [
      type: ArrayList,
      default: '',
      description: 'Set a variable in the Terraform configuration. This flag can be set multiple times',
    ],
    'varFile': [
      type: CharSequence,
      default: '',
      description: 'Set variables in the Terraform configuration from a file. If "terraform.tfvars" or any ".auto.tfvars" files are present, they will be automatically loaded.',
    ],
    'terraformVersion1': [
      type: Boolean,
      default: false,
      description: 'Indicate if we are running a version of terraform >= 1.',
    ]
  ]
}

def destroy(Map config = [:]){
  config.subCommand = 'destroy -no-color'

  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!(getDestroyValidParameters().containsKey(key) || getCommonValidParameters().containsKey(key))) {
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
    }
  }

  config.force = true
  println("Configuration: ${config}")
  terraform(config)
}

def fmt(Map config = [:]){
  config.subCommand = 'fmt -no-color'
  validParameters = [
    'list':'',
    'write':'',
    'diff':'',
    'check':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
  ]

  for(Iterator<Integer> iterator = config.keySet().iterator(); iterator.hasNext(); ) {
    key = iterator.next();
    if (!validParameters.containsKey(key)){
      config.remove(key)
      println("Since ${key} is not valid for ${config.subCommand}, we removed it. :)")
    }
  }
  println("Call terraform with config: ${config}")
  terraform(config)
}

Map getCommonValidParameters() {
  return [
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'dockerNetwork': '',
    'commandTarget':'',
    'throwError':'',
  ]
}

def call(Map config = [:]){
  Debugger debugger = IOC.get(Debugger.class.getName())
  debugger.printDebug("Terraform ${config.subCommand}: Starting checking config.")

  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/terraform:3.16.1')
  mapAttributeCheck(config, 'terraformVersion1', Boolean, false)
  mapAttributeCheck(config, 'subCommand', CharSequence, '', 'ERROR: The subcommand must be defined!')
  mapAttributeCheck(config, 'dockerAdditionalMounts', Map, [:])
  mapAttributeCheck(config, 'dockerEnvironmentVariables', Map, [:])
  mapAttributeCheck(config, 'dockerNetwork', CharSequence, 'bridge')
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')
  mapAttributeCheck(config, 'throwError', Boolean, true)
  mapAttributeCheck(config, 'planFile', CharSequence, '')

  debugger.printDebug("Terraform ${config.subCommand}: Initializing Classes.")

  OptionStringFactory optionStringFactory = IOC.get(OptionStringFactory.class.getName())
  optionStringFactory.createOptionString('=')
  DockerRunnerHelper dockerRunnerHelper = IOC.get(DockerRunnerHelper.class.getName())

  debugger.printDebug("Terraform ${config.subCommand}: Starting input validation.")

  debugger.printDebug("Terraform ${config.subCommand}: Validing backend configuration.")
  if ( config.containsKey('backend') ){
    optionStringFactory.addOption('-backend', config.backend, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing check configuration.")
  if ( config.containsKey('check') ){
    optionStringFactory.addOption('-check', config.check, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing list configuration.")
  if ( config.containsKey('list') ){
    optionStringFactory.addOption('-list', config.list, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing diff configuration.")
  if ( config.containsKey('diff') ){
    optionStringFactory.addOption('-diff', config.diff, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing write configuration.")
  if ( config.containsKey('write') ){
    optionStringFactory.addOption('-write', config.write, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing backendConfigs configuration.")
  if ( config.containsKey('backendConfigs') && config.backendConfigs && [] != config.backendConfigs ){
    optionStringFactory.addOption('-backend-config', config.backendConfigs, ArrayList)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing backup configuration.")
  if ( config.containsKey('backup') ){
    optionStringFactory.addOption('-backup', config.backup)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing backupOut configuration.")
  if ( config.containsKey('backupOut') ){
    optionStringFactory.addOption('-backend-out', config.backupOut)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing checkVariables configuration.")
  if ( config.containsKey('checkVariables') ){
    optionStringFactory.addOption('-check-variables', config.checkVariables, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing force configuration.")
  if ( config.containsKey('force') && config.force ){
    optionStringFactory.addOption('-auto-approve')
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing forceCopy configuration.")
  if ( config.containsKey('forceCopy') && config.forceCopy ){
    optionStringFactory.addOption('-force-copy')
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing fromModule configuration.")
  if ( config.containsKey('fromModule') && config.fromModule ){
    optionStringFactory.addOption('-from-module', config.fromModule)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing get configuration.")
  if ( config.containsKey('get') ){
    optionStringFactory.addOption('-get', config.get, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing getPlugins configuration.")
  if ( config.containsKey('getPlugins') ){
    optionStringFactory.addOption('-get-plugins', config.getPlugins, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing input configuration.")
  if ( config.containsKey('input') ){
    // NOTE: Since this is jenkins executing it, if input has been set, it must
    // be forced set to false.
    optionStringFactory.addOption('-input', 'false')
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing lock configuration.")
  if ( config.containsKey('lock') ){
    optionStringFactory.addOption('-lock', config.lock, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing lockTimeout configuration.")
  if ( config.containsKey('lockTimeout') && config.lockTimeout){
    optionStringFactory.addOption('-lock-timeout', config.lockTimeout)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing moduleDepth configuration.")
  if ( config.containsKey('moduleDepth') && config.moduleDepth ){
    optionStringFactory.addOption('-module-depth', config.moduleDepth, Integer)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing noColor configuration.")
  if ( config.containsKey('noColor') && config.noColor ){
    optionStringFactory.addOption('-no-color')
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing out configuration.")
  if ( config.containsKey('out') && config.out ){
    optionStringFactory.addOption('-out', config.out)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing parallelism configuration.")
  if ( config.containsKey('parallelism') && config.parallelism ){
    optionStringFactory.addOption('-parallelism', config.parallelism, Integer)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing pluginDirs configuration.")
  if ( config.containsKey('pluginDirs') && config.pluginDirs ){
    optionStringFactory.addOption('-plugin-dir', config.pluginDirs, ArrayList)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing reconfiure configuration.")
  if ( config.containsKey('reconfigure') && config.reconfigure ){
    optionStringFactory.addOption('-reconfigure')
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing refresh configuration.")
  if ( config.containsKey('refresh') ){
    optionStringFactory.addOption('-refresh', config.refresh, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing state configuration.")
  if ( config.containsKey('state') && config.state ){
    optionStringFactory.addOption('-state', config.state)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing stateOut configuration.")
  if ( config.containsKey('stateOut') ){
    optionStringFactory.addOption('-state-out', config.stateOut)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing targets configuration.")
  if ( config.containsKey('targets') && config.targets ){
    optionStringFactory.addOption('-target', config.targets, ArrayList)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing upgrade configuration.")
  if ( config.containsKey('upgrade') ){
    optionStringFactory.addOption('-upgrade', config.upgrade, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing varFile configuration.")
  if ( config.containsKey('varFile') && config.varFile ){
    optionStringFactory.addOption('-var-file', config.varFile)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing vars configuration.")
  if ( config.containsKey('vars') && config.vars ){
    optionStringFactory.addOption('-var', config.vars, ArrayList)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing verifyPlugins configuration.")
  if ( config.containsKey('verifyPlugins') ){
    optionStringFactory.addOption('-verify-plugins', config.verifyPlugins, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing json configuration.")
  if ( config.containsKey('json') ){
    optionStringFactory.addOption('-json', config.json, Boolean)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing module configuration.")
  if ( config.containsKey('module') && config.module ){
    optionStringFactory.addOption('-module', config.module)
  }
  debugger.printDebug("Terraform ${config.subCommand}: Validing moduleDepth configuration.")
  if ( config.containsKey('moduleDepth') && config.moduleDepth ){
    optionStringFactory.addOption('module-depth', config.moduleDepth, Integer)
  }

  // We're bind mounting the docker socket as well to support doing
  // local-exec with terraform.
  config.dockerAdditionalMounts.put('/var/run/docker.sock', '/var/run/docker.sock')

  debugger.printDebug("Terraform ${config.subCommand}: Validated all inputs.")

  if (debugger.debugVarExists()) {
    dockerRunnerHelper.prepareRunCommand(
      config.dockerImage,
      'terraform',
      "version",
      config.dockerAdditionalMounts,
      config.dockerEnvironmentVariables,
      config.dockerNetwork
    )

    dockerRunnerHelper.run()
  }

  debugger.printDebug("Going to run terrafrom ${config.subCommand} with the following configuration: ${config}")

  if ( config.terraformVersion1.toBoolean() ) {
    dockerRunnerHelper.prepareRunCommand(
      config.dockerImage,
      'terraform',
      "-chdir=\"${config.commandTarget}\" ${config.subCommand} ${optionStringFactory.getOptionString().toString()} ${config.planFile}",
      config.dockerAdditionalMounts,
      config.dockerEnvironmentVariables,
      config.dockerNetwork
    )
  } else {
    dockerRunnerHelper.prepareRunCommand(
      config.dockerImage,
      'terraform',
      "${config.subCommand} ${optionStringFactory.getOptionString().toString()} ${config.commandTarget}",
      config.dockerAdditionalMounts,
      config.dockerEnvironmentVariables,
      config.dockerNetwork
    )
  }

  debugger.printDebug("Terraform ${config.subCommand}: Prepared command with dockerRunnerHelper.")
  return dockerRunnerHelper.run(config.dockerImage,  config.throwError)
  debugger.printDebug("Terraform ${config.subCommand}: Ran command with dockerRunnerHelper.")
}
