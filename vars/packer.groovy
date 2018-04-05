def build(Map config){
  def color           = config.color           ?: 'true'
  def debug           = config.debug           ?: false
  def except          = config.except          ?: false
  def only            = config.only            ?: false
  def force           = config.force           ?: false
  def machineReadable = config.machineReadable ?: false
  def onError         = config.onError         ?: 'abort'
  def parallel        = config.parallel        ?: 'true'
  def vars            = config.vars            ?: []
  def varFile         = config.varFile         ?: false
  def dockerImage     = config.dockerImage     ?: 'fxinnovation/packer:latest'
  def dockerOptions   = config.dockerOptions   ?: ''
  def TemplateFile    = config.templateFile

  def packerCommand = 'packer build'
  try{
    sh "docker run --rm ${dockerImage} --version"
    packerCommand = "docker run --rm -v \$(pwd):/data -w /data ${dockerOptions} ${dockerImage} build"
  }catch(error){}

  packerCommand = packerCommand + " -color=${color} -on-error=${onError} -parallel=${parallel}"
  if ( debug == true ){
    packerCommand = packerCommand + " -debug"
  }
  if ( except != false ){
    packerCommand = packerCommand + " -except=${except}"
  }
  if ( only != false ){
    packerCommand = packerCommand + " -only=${only}"
  }
  if ( machineReadable == true ){
    packerCommand = packerCommand + " -machine-readable"
  }
  if ( varFile != false ){
    packerCommand = packerCommand + " -var-file=${varFile}"
  }
  vars.each{
    packerCommand = packerCommand + " -var '${it}'"
  }
  packerCommand = packerCommand + " ${templateFile}"

  output = command(packerCommand)
  return output
}

def validate(Map config){
  def syntaxOnly      = config.syntaxOnly      ?: false
  def except          = config.except          ?: false
  def only            = config.only            ?: false
  def vars            = config.vars            ?: []
  def varFile         = config.varFile         ?: false
  def dockerImage     = config.dockerImage     ?: 'fxinnovation/packer:latest'
  def dockerOptions   = config.dockerOptions   ?: ''
  def templateFile    = config.templateFile

  def packerCommand = 'packer build'
  try{
    sh "docker run --rm ${dockerImage} --version"
    packerCommand = "docker run --rm -v \$(pwd):/data -w /data ${dockerOptions} ${dockerImage} validate"
  }catch(error){}
  
  if ( syntaxOnly == true ){
    packerCommand = packerCommand + " -syntax-only"
  }
  if ( except != false ){
    packerCommand = packerCommand + " -except=${except}"
  }
  if ( only != false ){
    packerCommand = packerCommand + " -only=${only}"
  }
  if ( varFile != false ){
    packerCommand = packerCommand + " -var-file=${varFile}"
  }
  vars.each{
    packerCommand = packerCommand + " -var '${it}'"
  }
  packerCommand = packerCommand + " ${templateFile}"

  output = command(packerCommand)
  return output
}
