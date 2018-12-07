// terraform validate
// def validate(Map config = [:]){
//   if ( !config.containsKey('checkVariables') ){
//     config.checkVariables = 'true'
//   }
//   if ( !config.containsKey('noColor') ){
//     config.noColor = false
//   }
//   if ( !config.containsKey('vars') ){
//     config.vars = [:]
//   }
//   if ( !config.containsKey('varFile') ){
//     config.varFile = false
//   }
//   if ( !config.containsKey('directory') ){
//     config.directory = './'
//   }
//   if ( !config.containsKey('dockerImage') ){
//     config.dockerImage = 'fxinnovation/terraform:latest'
//   }
// 
//   def terraformCommand = 'terraform validate'
//   try{
//     sh "docker run --rm ${config.dockerImage} --version"
//     terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage} validate"
//   }catch(error){}
// 
//   terraformCommand = terraformCommand + " -check-variables=${config.checkVariables}"
//   if ( config.noColor == true ){
//     terraformCommand = terraformCommand + " -no-color"
//   }
//   if ( config.varFile != false ){
//     terraformCommand = terraformCommand + " -var-file=${config.varFile}"
//   }
//   for ( var in config.vars ){
//     terraformCommand = terraformCommand + " -var '${var}'"
//   }
// 
//   terraformCommand = terraformCommand + " ${config.directory}"
// 
//   output = command(terraformCommand)
//   return output
// }
// 
def init(Map config = [:]){
  config.subCommand = 'init'
  terraform(config)
}

// def plan(Map config){
//   def noColor        = config.noColor        ?: false
//   def vars           = config.vars           ?: []
//   def varFile        = config.varFile        ?: false
//   def directory      = config.directory      ?: './'
//   def out            = config.out            ?: false
//   def destroy        = config.destroy        ?: false
//   def lock           = config.lock           ?: 'true'
//   def lockTimeout    = config.lockTimeout    ?: '0s'
//   def moduleDepth    = config.moduleDepth    ?: '-1'
//   def parallelism   = config.parallelism   ?: '10'
//   def refresh        = config.refresh        ?: 'true'
//   def state          = config.state          ?: false
//   def targets        = config.targets        ?: []
//   def directory      = config.directory      ?: './'
//   def dockerImage    = config.dockerImage    ?: 'fxinnovation/terraform:latest'
// 
//   def terraformCommand = 'terraform plan'
//   try{
//     sh "docker run --rm ${dockerImage} --version"
//     terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${dockerImage} plan"
//   }catch(error){}
// 
//   terraformCommand = terraformCommand + " -lock=${lock} -lock-timeout=${lockTimeout} -parallelism=${parallelism} -refresh=${refresh}"
//   if ( noColor == true ){
//     terraformCommand = terraformCommand + " -no-color"
//   }
//   vars.each{
//     terraformCommand = terraformCommand + " -var '${it}'"
//   }
//   if ( varFile != false ){
//     terraformCommand = terraformCommand + " -var-file='${varFile}'"
//   }
//   if ( out != false ){
//     terraformCommand = terraformCommand + " -out='${out}'"
//   }
//   if ( destroy == true ){
//     terraformCommand = terraformCommand + " -destroy"
//   }
//   targets.each{
//     terraformCommand = terraformCommand + " -target='${it}'"
//   }
//   if ( state != false ){
//     terraformCommand = terraformCommand + " -state='${state}'"
//   }
//   terraformCommand = terraformCommand + " ${directory}"
// 
//   output = command(terraformCommand)
//   return output
// }
// 
// def apply(Map config){
//   def backup         = config.backup         ?: false
//   def lock           = config.lock           ?: 'true'
//   def lockTimeout    = config.lockTimeout    ?: '0s'
//   def noColor        = config.noColor        ?: false
//   def parallelism    = config.parallelism    ?: '10'
//   def refresh        = config.refresh        ?: 'true'
//   def state          = config.state          ?: false
//   def stateOut       = config.stateOut       ?: false
//   def targets        = config.targets        ?: []
//   def vars           = config.vars           ?: []
//   def varFile        = config.varFile        ?: false
//   def directory      = config.directory      ?: './'
//   def plan           = config.plan           ?: false
//   def dockerImage    = config.dockerImage    ?: 'fxinnovation/terraform:latest'
// 
//   def terraformCommand = 'terraform apply'
// 
//   try{
//     sh "docker run --rm ${dockerImage} --version"
//     terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${dockerImage} apply"
//   }catch(error){}
// 
//   terraformCommand = terraformCommand + " -auto-approve=true -input=false -lock=${lock} -lock-timeout=${lockTimeout} -parallelism=${parallelism}i -refresh=${refresh}"
//   if ( backup != false ){
//     terraformCommand = terraformCommand + " -backup='${backup}'"
//   }
//   if ( noColor == true ){
//     terraformCommand = terraformCommand + ' -no-color'
//   }
//   if ( state != false ){
//     terraformCommand = terraformCommand + " -state='${state}'"
//   }
//   if ( stateOut != false ){
//     terraformCommand = terraformCommand + " -state-out='${stateOut}'"
//   }
//   targets.each{
//     terraformCommand = terraformCommand + " -target='${it}'"
//   }
//   vars.each{
//     terraformCommand = terraformCommand + " -var '${it}'"
//   }
//   if ( varFile != false ){
//     terraformCommand = terraformCommand + " -var-file='${varFile}'"
//   }
//   if ( plan != false ){
//     terraformCommand = terraformCommand + " ${plan}"
//   }else{
//     terraformCommand = terraformCommand + " ${directory}"
//   }
// 
//   output = command(terraformCommand)
//   return output
// }
// 
// def destroy(Map config){
//   def backup         = config.backup         ?: false
//   def lock           = config.lock           ?: 'true'
//   def lockTimeout    = config.lockTimeout    ?: '0s'
//   def noColor        = config.noColor        ?: false
//   def parallelism    = config.parallelism    ?: '10'
//   def refresh        = config.refresh        ?: 'true'
//   def state          = config.state          ?: false
//   def stateOut       = config.stateOut       ?: false
//   def targets        = config.targets        ?: []
//   def vars           = config.vars           ?: []
//   def varFile        = config.varFile        ?: false
//   def directory      = config.directory      ?: './'
//   def dockerImage    = config.dockerImage    ?: 'fxinnovation/terraform:latest'
// 
//   def terraformCommand = 'terraform destroy'
// 
//   try{
//     sh "docker run --rm ${dockerImage} --version"
//     terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${dockerImage} destroy"
//   }catch(error){}
// 
//   terraformCommand = terraformCommand + " -auto-approve=true -input=false -lock=${lock} -lock-timeout=${lockTimeout} -parallelism=${parallelism}i -refresh=${refresh}"
//   if ( backup != false ){
//     terraformCommand = terraformCommand + " -backup='${backup}'"
//   }
//   if ( noColor == true ){
//     terraformCommand = terraformCommand + ' -no-color'
//   }
//   if ( state != false ){
//     terraformCommand = terraformCommand + " -state='${state}'"
//   }
//   if ( stateOut != false ){
//     terraformCommand = terraformCommand + " -state-out='${stateOut}'"
//   }
//   targets.each{
//     terraformCommand = terraformCommand + " -target='${it}'"
//   }
//   vars.each{
//     terraformCommand = terraformCommand + " -var '${it}'"
//   }
//   if ( varFile != false ){
//     terraformCommand = terraformCommand + " -var-file='${varFile}'"
//   }
//   terraformCommand = terraformCommand + " ${directory}"
// 
//   output = command(terraformCommand)
//   return output
// }
// 
// def taint(Map config){
//   def allowMissing   = config.allowMissing   ?: false
//   def backup         = config.backup         ?: false
//   def lock           = config.lock           ?: 'true'
//   def lockTimeout    = config.lockTimeout    ?: '0s'
//   def module         = config.module         ?: false
//   def noColor        = config.noColor        ?: false
//   def state          = config.state          ?: false
//   def stateOut       = config.stateOut       ?: false
//   def dockerImage    = config.dockerImage    ?: 'fxinnovation/terraform:latest'
//   def resource       = config.resource
// 
//   def terraformCommand = 'terraform taint'
// 
//   try{
//     sh "docker run --rm ${dockerImage} --version"
//     terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${dockerImage} taint"
//   }catch(error){}
// 
//   terraformCommand = terraformCommand + " -lock=${lock} -lock-timeout=${lockTimeout}"
// 
//   if ( allowMissing == true ){
//     terraformCommand = terraformCommand + ' -allow-missing'
//   }
//   if ( backup != false ){
//     terraformCommand = terraformCommand + " -backup='${backup}'"
//   }
//   if ( module != false ){
//     terraformCommand = terraformCommand + " -module='${module}'"
//   }
//   if ( noColor == true ){
//     terraformCommand = terraformCommand + ' -no-color'
//   }
//   if ( state != false ){
//     terraformCommand = terraformCommand + " -state='${state}'"
//   }
//   if ( stateOut != false ){
//     terraformCommand = terraformCommand + " -state-out='${stateOut}'"
//   }
// 
//   terraformCommand = terraformCommand + " ${resource}"
// 
//   output = command(terraformCommand)
//   return output
// }
// 
// def untaint(Map config){
//   def allowMissing   = config.allowMissing   ?: false
//   def backup         = config.backup         ?: false
//   def lock           = config.lock           ?: 'true'
//   def lockTimeout    = config.lockTimeout    ?: '0s'
//   def module         = config.module         ?: false
//   def noColor        = config.noColor        ?: false
//   def state          = config.state          ?: false
//   def stateOut       = config.stateOut       ?: false
//   def dockerImage    = config.dockerImage    ?: 'fxinnovation/terraform:latest'
//   def resource       = config.resource
// 
//   def terraformCommand = 'terraform untaint'
// 
//   try{
//     sh "docker run --rm ${dockerImage} --version"
//     terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${dockerImage} untaint"
//   }catch(error){}
// 
//   terraformCommand = terraformCommand + " -lock=${lock} -lock-timeout=${lockTimeout}"
// 
//   if ( allowMissing == true ){
//     terraformCommand = terraformCommand + ' -allow-missing'
//   }
//   if ( backup != false ){
//     terraformCommand = terraformCommand + " -backup='${backup}'"
//   }
//   if ( module != false ){
//     terraformCommand = terraformCommand + " -module='${module}'"
//   }
//   if ( noColor == true ){
//     terraformCommand = terraformCommand + ' -no-color'
//   }
//   if ( state != false ){
//     terraformCommand = terraformCommand + " -state='${state}'"
//   }
//   if ( stateOut != false ){
//     terraformCommand = terraformCommand + " -state-out='${stateOut}'"
//   }
// 
//   terraformCommand = terraformCommand + " ${resource}"
// 
//   output = command(terraformCommand)
//   return output
// }

def call(Map config = [:]){
  if ( !config.containsKey('dockerImage') ){
    config.dockerImage = "fxinnovation/terraform:latest"
  }
  if ( !config.containsKey('subCommand') ){
    error('ERROR: The subcommand must be defined!')
  }

  def optionsString = ''
  if ( config.containsKey('backend') ){
    if ( config.backend instanceof Boolean ){
      optionsString = optionsString + "-backend=${config.backend} "
    }else{
      error('terraform - "backend" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('backendConfigs') ){
    if ( !config.backendConfigs instanceof String[] ){
      error('terraform - "backendConfigs" parameter must be of type "String[]"')
    }
    for (i=0; i>config.backendConfigs.size(); i++){
      optionsString = optionsString + "-backend-config=${config.backendConfig[i]} "
    }
  }
  if ( config.containsKey('forceCopy') ){
    if ( config.forceCopy instanceof Boolean ){
      if ( config.forceCopy ){
        optionsString = optionsString + "-force-copy "
      }
    }else{
      error('terraform - "forceCopy" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('fromModule') ){
    if ( config.fromModule instanceof String) {
      optionsString = optionsString + "-from-module=${config.fromModule} "
    }else{
      error('terraform - "fromModule" parameter must be of type "String"')
    }
  }
  if ( config.containsKey('get') ){
    if ( config.get instanceof Boolean ){
      optionsString = optionsString + "-get=${config.get} "
    }else{
      error('terraform - "get" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('getPlugins') ){
    if ( config.getPlugins instanceof Boolean ){
      optionsString = optionsString + "-get-plugins=${config.getPlugins} "
    }else{
      error('terraform - "getPlugins" parameter must be of type "Boolean"')
    }
  }
  optionsString = optionsString + "-input=false "
  if ( config.containsKey('lock') ){
    if ( config.lock instanceof Boolean ){
      optionsString = optionsString + "-lock=${config.lock} "
    }else{
      error('terraform - "lock" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('lockTimeout') ){
    if (config.lockTimeout instanceof String ){
      optionsString = optionsString + "-lock-timeout=${config.lockTimeout} "
    }else{
      error('terraform - "lockTimeout" parameter must be of type "String"')
    }
  }
  if ( config.containsKey('noColor') ){
    if ( config.noColor instanceof Boolean ){
      if ( config.noColor ){
        optionsString = optionsString + "-no-color "
      }
    }else{
      error('terraform - "noColor" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('pluginDirs') ){
    if ( !config.pluginDirs instanceof String[] ){
      error('terraform - "pluginDirs" parameter must be of type "String[]"')
    }
    for (i=0; i>config.pluginDirs.size(); i++){
      optionsString = optionsString + "-plugin-dir ${config.pluginDirs[i]} "
    }
  }
  if ( config.containsKey('reconfigure') ){
    if ( config.reconfigure instanceof Boolean ){
      if ( config.reconfigure ){
        optionsString = optionsString + "-reconfigure "
      }
    }else{
      error('terraform - "reconfigure" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('upgrade') ){
    if ( config.upgrade instanceof Boolean ){
      optionsString = optionsString + "-upgrade=${config.upgrade} "
    }else{
      error('terraform - "upgrade" parameter must be of type "Boolean"')
    }
  }
  if ( config.containsKey('verifyPlugins') ){
    if ( config.verifyPlugins instanceof Boolean ){
      optionsString = optionsString + "-verify-plugins=${config.verifyPlugins} "
    }else{
      error('terraform - "verifyPlugins" parameter must be of type "Boolean"')
    }
  }

  try {
    sh "docker version"
    terraformCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage}"
    sh "docker pull ${config.dockerImage}"
  } catch(dockerError) {
    println 'Docker is not available, assuming terraform is installed'
    terraformCommand = 'terraform'
  }

  terraformVersion = sh(
    returnStdout: true,
    script:       "${terraformCommand} version"
  ).trim()

  println "Terraform version is:\n${terraformVersion}"

  sh "${terraformCommand} ${config.subCommand} ${config.optionsString}"
}
