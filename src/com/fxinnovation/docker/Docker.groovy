package com.fxinnovation.docker

/**
* Helpers for using docker
*/

class Docker {

  Object context

  Docker(
    Object context
  ){
    this.context = context
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
