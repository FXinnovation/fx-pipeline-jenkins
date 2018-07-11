def call(Map config = [:]){
  if (!config.containsKey('dockerImage')) {
    config.dockerImage = 'fxinnovation/pythonunittest:latest'
  }
  if (!config.containsKey('options')) {
    config.options = ''
  }
  if (!config.containsKey('filePattern')) {
    config.filePattern = 'test.py'
  }
  if (!config.containsKey('testResultFile')) {
    config.testResultFile = 'junit_results.xml'
  }

  def output = ''
  def dockerCommand = "pytest --junitxml ${config.testResultFile}"
  def testCommand = "pytest --junitxml ${config.testResultFile}"
  try {
    sh "docker run --rm ${config.dockerImage} ${dockerCommand} --version"
    testCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage} ${dockerCommand}"
  } catch (error) {
    println "docker was not found. Falling back to the test command ”${testCommand}“"
  }

  output = command("${testCommand} ${config.options} ${config.filePattern}").trim()
  return output
}
