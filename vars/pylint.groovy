def call(Map config = [:]) {
  File currentScript = new File(getClass().protectionDomain.codeSource.location.path)

  if (!config.containsKey('dockerImage')) {
    config.dockerImage = 'fxinnovation/pythonlinters:latest'
  }
  if (!config.containsKey('options')) {
    config.options = ''
  }
  if (!config.containsKey('filePattern')) {
    error(currentScript.getName() + ' - filePattern parameter is mandatory.')
  }
  if (!config.containsKey('linterOptionsRepo')) {
    config.linterOptionsRepo = 'https://bitbucket.org/fxadmin/public-common-configuration-linters.git'
  }
  if (!config.containsKey('linterOptionsRepoCredentialsId')) {
    error(currentScript.getName() + ' - linterOptionsRepoCredentialsId parameter is mandatory.')
  }
  if (!config.containsKey('linterOptionsRepoBranchPattern')) {
    config.linterOptionsRepoBranchPattern = '*/master'
  }

  dir('pylint') {
    checkout([
      $class                           : 'GitSCM',
      branches                         : [[name: "${config.linterOptionsRepoBranchPattern}"]],
      extensions                       : [],
      submoduleCfg                     : [],
      doGenerateSubmoduleConfigurations: false,
      userRemoteConfigs                : [
        [credentialsId: "${config.linterOptionsRepoCredentialsId}", url: "${config.linterOptionsRepo}"]
      ]
    ])
  }

  def output = ''
  def dockerCommand = 'pylint'
  def configurationOptions = '--rcfile pylint/.pylintrc'
  def testCommand = 'pylint'
  try {
    sh "docker run --rm ${config.dockerImage} --version"
    testCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage} ${dockerCommand}"
  } catch (error) {
    println "docker was not found. Falling back to the test command ”${testCommand}“"
  }

  output = command("${testCommand} ${configurationOptions} ${config.options} ${config.filePattern}").trim()
  return output
}
