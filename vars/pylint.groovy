def call(Map config = [:]){
    File currentScript = new File(getClass().protectionDomain.codeSource.location.path)

    if ( !config.containsKey('dockerImage') ){
        config.dockerImage = 'fxinnovation/pythonlinters:latest'
    }
    if ( !config.containsKey('options') ){
        config.options = ''
    }
    if ( !config.containsKey('filePattern') ){
        error(currentScript.getName() + ' - filePattern parameter is mandatory')
    }
    if ( !config.containsKey('pylintRepository') ){
        config.pylintRepository = 'https://bitbucket.org/fxadmin/public-common-configuration-linters.git'
    }

    dir('pylint') {
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, userRemoteConfigs: [[credentialsId: 'temp_bitbucket_credentials', url: "${config.pylintRepository}"]]])
    }

    def output = ''
    def dockerCommand = 'pylint'
    def configurationOption = '--rcfile pylint/.pylintrc'
    def testCommand = 'pylint'
    try{
        sh "docker run --rm ${config.dockerImage} --version"
        testCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage} ${dockerCommand} ${configurationOption}"
    } catch (error) {
        println error
    }

    println "${testCommand} ${config.options} ${config.filePattern}"
    output = command("${testCommand} ${config.options} ${config.filePattern}").trim()
    return output
}
