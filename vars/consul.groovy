def command(config){
  def httpAddr = config.httpAddr ?: 'http://consul:8500'
  def command  = config.command
  def version  = config.version ?: 'latest'
  def dockerImage = config.dockerImage ?: 'consul'

  def consulCommand = 'consul'
  def output        = ''

  try{
    sh "docker run --rm ${dockerImage}:${version} --version"
    consulCommand = "docker run --rm ${dockerImage}:${version}"
  }catch(error){}
  try{
    output = command("${consulCommand} ${command}")
  }catch(error){
    error(output)
  }
  return output
}

def put(Map config){
  def httpAddr = config.httpAddr ?: 'http://consul:8500'
  def key      = config.key
  def value    = config.value

  consul.command(
    command:  "kv put ${key} ${value}",
    httpAddr: httpAddr,
  )
}

def get(Map config){
  def httpAddr = config.httpAddr ?: 'http://consul:8500'
  def key      = config.key

  output = consul.command(
    command:  "kv put ${key}",
    httpAddr: httpAddr,
  ).trim()

  return output
}
