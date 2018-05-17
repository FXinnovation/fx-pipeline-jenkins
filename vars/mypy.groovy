def call(Map config = [:]){
  File currentScript = new File(getClass().protectionDomain.codeSource.location.path)

  if (!config.containsKey('dockerImage')) {
    config.dockerImage = 'fxinnovation/pythonlinters:latest'
  }
  if (!config.containsKey('options')) {
    config.options = ''
  }
  if (!config.containsKey('filePattern')) {
    error(currentScript.getName() + ' - filePattern parameter is mandatory')
  }

  def output = ''
  def dockerCommand = 'mypy'
  def testCommand = 'mypy'
  try {
    sh "docker run --rm ${config.dockerImage} ${dockerCommand} --version"
    testCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage} ${dockerCommand}"
  } catch (error) {
    println error
  }

  println "${testCommand} ${config.options} ${config.filePattern}"
  output = command("${testCommand} ${config.options} ${config.filePattern}").trim()
  return output
}
