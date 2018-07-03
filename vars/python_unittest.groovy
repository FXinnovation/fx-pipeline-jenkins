def call(Map config = [:]){
  if (!config.containsKey('dockerImage')) {
    config.dockerImage = 'fxinnovation/pythonunittest:latest'
  }
  if (!config.containsKey('options')) {
    config.options = ''
  }
  if (!config.containsKey('filePattern')) {
    config.filePattern = ''
  }

  def output = ''
  def dockerCommand = 'python3 -m unittest'
  def testCommand = 'python3 -m unittest'
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
