def execute(config){
  def httpAddr    = config.httpAddr ?: 'http://consul:8500'
  def command     = config.command
  def version     = config.version ?: 'latest'
  def dockerImage = config.dockerImage ?: 'consul'

  def consulCommand = 'consul'
  def output        = ''
  def cmd = ''

  try{
    sh "docker run --rm ${dockerImage}:${version} --version"
    consulCommand = "docker run --rm ${dockerImage}:${version}"
  }catch(error){}
  try{
    cmd = "${consulCommand} ${command}"
    output = command(cmd)
  }catch(error){
    error(output)
  }
  return output
}

def put(config){
  def httpAddr = config.httpAddr ?: 'http://consul:8500'
  def key      = config.key
  def value    = config.value

  consul.execute(
    command:  "kv put ${key} ${value}",
    httpAddr: httpAddr,
  )
}

def get(config){
  def httpAddr = config.httpAddr ?: 'http://consul:8500'
  def key      = config.key

  output = consul.execute(
    command:  "kv get ${key}",
    httpAddr: httpAddr,
  ).trim()

  return output
}
