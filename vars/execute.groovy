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
       touch /tmp/${filePrefix}-all.log
       touch /tmp/${filePrefix}-stdout.log
       touch /tmp/${filePrefix}-stderr.log
       touch /tmp/${filePrefix}-statuscode
       tail -f /tmp/$filePrefix-all.log &
       TAIL_PID=\$!
       ((${config.script} | tee /tmp/${filePrefix}-stdout.log) 3>&1 1>&2 2>&3 | tee /tmp/${filePrefix}-stderr.log) &> /tmp/${filePrefix}-all.log
       foo=\${PIPESTATUS[@]}
       # echo \${PIPESTATUS[0]} > /tmp/${filePrefix}-statuscode
       echo 0 > /tmp/${filePrefix}-statuscode
       # echo \${PIPESTATUS[@]}
       kill \${TAIL_PID} > /dev/null
       rm /tmp/${filePrefix}-all.log
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
