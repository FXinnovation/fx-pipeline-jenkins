package com.fxinnovation.docker

/**
* Helper for docker
*/

class Docker {

  Object context

  Json2hcl(
    Object context
  ){
    this.context = context
    this.dockerImage = dockerImage
  }

  /**
  * Checks if docker is available in current context
  * @return Boolean
  */
  public Boolean isAvailable(){
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

  /**
  * Pull a docker image in current context
  * @param String docker image to pull
  */
  public void pullImage(String dockerImage){
    this.context.sh(
      returnStdout: true,
      script:       "docker pull ${dockerImage}"
    )
  }
}
