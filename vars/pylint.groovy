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
    if ( !config.containsKey('linterOptionsRepo') ){
        config.linterOptionsRepo = 'https://bitbucket.org/fxadmin/public-common-configuration-linters.git'
    }
    if ( !config.containsKey('linterOptionsRepoCredentialsId') ){
        config.linterOptionsRepoCredentialsId = 'temp_bitbucket_credentials'
    }
    if ( !config.containsKey('linterOptionsRepoBranchPattern') ){
        config.linterOptionsRepoBranchPattern = '*/master'
    }

    dir('pylint') {
        checkout([$class: 'GitSCM', branches: [[name: "${config.linterOptionsRepoBranchPattern}"]], doGenerateSubmoduleConfigurations: false, userRemoteConfigs: [[credentialsId: "${config.linterOptionsRepoCredentialsId}", url: "${config.pylintRepository}"]]])
    }

    def output = ''
    def dockerCommand = 'pylint'
    def configurationOption = '--rcfile pylint/.pylintrc'
    def testCommand = 'pylint'
    try{
        sh "docker run --rm ${config.dockerImage} --version"
        testCommand = "docker run --rm -v \$(pwd):/data -w /data ${config.dockerImage} ${dockerCommand}"
    } catch (error) {
        println error
    }

    output = command("${testCommand} ${configurationOption} ${config.options} ${config.filePattern}").trim()
    return output
}
