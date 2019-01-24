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
       set -x
       touch /tmp/${filePrefix}-stdout.log
       touch /tmp/${filePrefix}-stderr.log
       touch /tmp/${filePrefix}-statuscode
       tail -f /tmp/$filePrefix-stdout.log &
       STDOUT_PID=\$!
       tail -f /tmp/$filePrefix-stderr.log &
       STDERR_PID=\$!
       ${config.script} >> /tmp/$filePrefix-stdout.log 2>> /tmp/$filePrefix-stderr.log
       echo $? > /tmp/${filePrefix}-statuscode
       kill \${STDOUT_PID} &> /dev/null
       kill \${STDERR_PID} &> /dev/null
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
