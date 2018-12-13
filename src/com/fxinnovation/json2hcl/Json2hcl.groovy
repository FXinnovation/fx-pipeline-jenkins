package com.fxinnovation.json2hcl

/**
 * Helper for json2hcl
 */
class Json2hcl {

  Object context
  String dockerImage
  final JSON_2_HCL_COMMAND = 'json2hcl'

  Json2Hcl(
    Object context,
    String dockerImage = 'fxinnovation/json2hcl:latest'
  ){
    this.context = context
    this.dockerImage = dockerImage
  }

  /**
  * Transforms a $json String into a HCL String
  * @param json JSON string to transform
  * @return HCL string
  */
  public String transform(String json){
    return this.context.sh(
      returnStdout: true,
      script:       this.getExecCommand(json)
    ).trim()
  }

  /**
  * Transforms a $hcl String into a json String
  * @param hcl HCL string to transform
  * @return JSON string
  */
  public String reverseTranform(String hcl){
    return this.context.sh(
      returnStdout: true,
      script:       this.getExecCommand(hcl, '-reverse') 
    ).trim()
  }

  private Boolean isDockerAvailable(){
    try {
      this.context.sh(
        returnStdout: true,
        script:       'docker --version'
      )
      return true
    }catch(error){
      if ( 'script returned exit code 127' != error.getMessage() ){
        throw error 
      }
      return false
    }
  }

  private void pullImage(){
    this.context.sh(
      returnStdout: true,
      script:       "docker pull ${this.dockerImage}"
    )
  }

  private String getExecCommand(String input, String optionsString = ''){
    if ( this.isDockerAvailable() ){
      return "echo '${input}' | docker run --rm -i ${this.dockerImage} ${optionsString}"
    }else{
      return "echo '${input}' | ${this.JSON_2_HCL_COMMAND} ${optionsString}"
    }
  }
}
