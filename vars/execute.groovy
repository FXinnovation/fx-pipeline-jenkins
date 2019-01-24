def call(Map config = [:]){
  if (!config.containsKey('script') || !config.script instanceof String){
    error('"script" parameter is mandatory and must be a String')
  } 
  if (!config.containsKey('throwError') || !config.throwError instanceof Boolean){
    config.throwError = true
  }

  filePrefix = new Date().getTime()
  response = [
    stdout: null,
    stderr: null,
    statusCode: null
  ]
  println "Executing: '${config.script}'"
  try{
    sh """
       set +x
       echo "" > /tmp/${filePrefix}-stdout.log
       echo "" > /tmp/${filePrefix}-stderr.log
       echo "" > /tmp/${filePrefix}-statuscode
       tail -f /tmp/${filePrefix}-stdout.log &
       STDOUT_PID=\$!
       tail -f /tmp/${filePrefix}-stderr.log &
       STDERR_PID=\$!
       set +e
       ${config.script} >> /tmp/${filePrefix}-stdout.log 2>> /tmp/${filePrefix}-stderr.log
       echo \$? > /tmp/${filePrefix}-statuscode
       set -e
       # This sleep is needed to make sure both stdout and stderr have been outputed on jenkins... :(
       sleep 1
       kill \${STDOUT_PID} &> /dev/null
       kill \${STDERR_PID} &> /dev/null
       exit \$(cat /tmp/${filePrefix}-statuscode)
       """
  }catch(error){
    if (config.trhowError){
      throw error
    }
  }finally{
    response.stdout = sh(
      returnStdout: true,
      script: "set +x; cat /tmp/${filePrefix}-stdout.log; rm /tmp/${filePrefix}-stdout.log"
    ).trim()
    response.stderr = sh(
      returnStdout: true,
      script: "set +x; cat /tmp/${filePrefix}-stderr.log; rm /tmp/${filePrefix}-stderr.log"
    ).trim()
    response.statusCode = sh(
      returnStdout: true,
      script: "set +x; cat /tmp/${filePrefix}-statuscode; rm /tmp/${filePrefix}-statuscode"
    ).trim().toInteger()

    return response
  }
}
