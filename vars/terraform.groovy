import com.fxinnovation.utils.OptionString

def show(Map config = [:]){
  config.subCommand = 'show'
  validParameters = [
    'noColor':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def output(Map config = [:]){
  config.subCommand = 'output'
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
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def validate(Map config = [:]){
  config.subCommand = 'validate'
  validParameters = [
    'checkVariables':'',
    'noColor':'',
    'vars':'',
    'varFile':'',
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
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
  for ( parameter in config ) {
    if ( !(getStateValidParameters().containsKey(parameter.key) || getCommonValidParameters().containsKey(parameter.key))){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
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
  config.subCommand = 'refresh'
  for ( parameter in config ) {
    if ( !(getRefreshValidParameters().containsKey(parameter.key) || getCommonValidParameters().containsKey(parameter.key))){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def slowRefresh(Map config = [:]){
  config.subCommand = 'refresh'
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
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
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
  ]
}

def init(Map config = [:]){
  config.subCommand = 'init'
  for ( parameter in config ) {
    if ( !(getInitValidParameters().containsKey(parameter.key) || getCommonValidParameters().containsKey(parameter.key))){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

def plan(Map config = [:]){
  config.subCommand = 'plan'
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
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  config.input=false
  terraform(config)
}

def apply(Map config = [:]){
  config.subCommand = 'apply'
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
  ]
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  config.autoApprove=true
  config.input=false
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
    ]
  ]
}

def destroy(Map config = [:]){
  config.subCommand = 'destroy'
  for ( parameter in config ) {
    if ( !(getDestroyValidParameters().containsKey(parameter.key) || getCommonValidParameters().containsKey(parameter.key))){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  config.force = true
  terraform(config)
}

def fmt(Map config = [:]){
  config.subCommand = 'fmt'
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
  for ( parameter in config ) {
    if ( !validParameters.containsKey(parameter.key)){
      error("terraform - Parameter \"${parameter.key}\" is not valid for \"${config.subCommand}\", please remove it!")
    }
  }
  terraform(config)
}

Map getCommonValidParameters() {
  return [
    'dockerImage':'',
    'subCommand':'',
    'dockerAdditionalMounts':'',
    'dockerEnvironmentVariables':'',
    'commandTarget':'',
    'throwError':'',
  ]
}

def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/terraform:latest')
  mapAttributeCheck(config, 'subCommand', CharSequence, '', 'ERROR: The subcommand must be defined!')
  mapAttributeCheck(config, 'dockerAdditionalMounts', Map, [:])
  mapAttributeCheck(config, 'dockerEnvironmentVariables', Map, [:])
  mapAttributeCheck(config, 'commandTarget', CharSequence, '')
  mapAttributeCheck(config, 'throwError', Boolean, true)

  def optionsString = new OptionString(this)
  optionsString.setDelimiter('=')

  if ( config.containsKey('backend') ){
    optionsString.add('-backend', config.backend, Boolean)
  }
  if ( config.containsKey('check') ){
    optionsString.add('-check', config.check, Boolean)
  }
  if ( config.containsKey('list') ){
    optionsString.add('-list', config.list, Boolean)
  }
  if ( config.containsKey('diff') ){
    optionsString.add('-diff', config.diff, Boolean)
  }
  if ( config.containsKey('write') ){
    optionsString.add('-write', config.write, Boolean)
  }
  if ( config.containsKey('backendConfigs') && config.backendConfigs ){
    optionsString.add('-backend-config', config.backendConfigs, ArrayList)
  }
  if ( config.containsKey('backup') ){
    optionsString.add('-backup', config.backup)
  }
  if ( config.containsKey('backupOut') ){
    optionsString.add('-backend-out', config.backupOut)
  }
  if ( config.containsKey('checkVariables') ){
    optionsString.add('-check-variables', config.checkVariables, Boolean)
  }
  if ( config.containsKey('force') && config.force ){
    optionsString.add('-force')
  }
  if ( config.containsKey('forceCopy') && config.forceCopy ){
    optionsString.add('-force-copy')
  }
  if ( config.containsKey('fromModule') && config.fromModule ){
    optionsString.add('-from-module', config.fromModule)
  }
  if ( config.containsKey('get') ){
    optionsString.add('-get', config.get, Boolean)
  }
  if ( config.containsKey('getPlugins') ){
    optionsString.add('-get-plugins', config.getPlugins, Boolean)
  }
  if ( config.containsKey('input') ){
    // NOTE: Since this is jenkins executing it, if input has been set, it must
    // be forced set to false.
    optionsString.add('-input', 'false')
  }
  if ( config.containsKey('lock') ){
    optionsString.add('-lock', config.lock, Boolean)
  }
  if ( config.containsKey('lockTimeout') && config.lockTimeout){
    optionsString.add('-lock-timeout', config.lockTimeout)
  }
  if ( config.containsKey('moduleDepth') && config.moduleDepth ){
    optionsString.add('-module-depth', config.moduleDepth, Integer)
  }
  if ( config.containsKey('noColor') && config.noColor ){
    optionsString.add('-no-color')
  }
  if ( config.containsKey('out') && config.out ){
    optionsString.add('-out', config.out)
  }
  if ( config.containsKey('parallelism') && config.parallelism ){
    optionsString.add('-parallelism', config.parallelism, Integer)
  }
  if ( config.containsKey('pluginDirs') && config.pluginDirs ){
    optionsString.add('-plugin-dir', config.pluginDirs, ArrayList)
  }
  if ( config.containsKey('reconfigure') && config.reconfigure ){
    optionsString.add('-reconfigure')
  }
  if ( config.containsKey('refresh') ){
    optionsString.add('-refresh', config.refresh, Boolean)
  }
  if ( config.containsKey('state') && config.state ){
    optionsString.add('-state', config.state)
  }
  if ( config.containsKey('stateOut') ){
    optionsString.add('-state-out', config.stateOut)
  }
  if ( config.containsKey('targets') && config.targets ){
    optionsString.add('-targets', config.targets, ArrayList)
  }
  if ( config.containsKey('upgrade') ){
    optionsString.add('-upgrade', config.upgrade, Boolean)
  }
  if ( config.containsKey('varFile') && config.varFile ){
    optionsString.add('-var-file', config.varFile)
  }
  if ( config.containsKey('vars') && config.vars ){
    optionsString.add('-var', config.vars, ArrayList)
  }
  if ( config.containsKey('verifyPlugins') ){
    optionsString.add('-verify-plugins', config.verifyPlugins, Boolean)
  }
  if ( config.containsKey('json') ){
    optionsString.add('-json', config.json, Boolean)
  }
  if ( config.containsKey('module')&& config.module ){
    optionsString.add('-module', config.module)
  }

  terraformCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand:  'terraform',
    additionalMounts: config.dockerAdditionalMounts,
    environmentVariables: config.dockerEnvironmentVariables,
  )

  execute(
    script: "${terraformCommand} version"
  )

  return execute(
    throwError: config.throwError,
    script: "${terraformCommand} ${config.subCommand} ${optionsString.toString()} ${config.commandTarget}"
  )
}
