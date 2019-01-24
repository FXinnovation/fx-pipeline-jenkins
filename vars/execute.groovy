def call(Map config = [:]){
  if (!config.containsKey('script')){
    error('"script" parameter is mandatory.')
  } 

  filePrefix = new Date().getTime()
  response = [
    stdout: null,
    stderr: null,
    statusCode: null
  ]
  println "Executing: '${config.script}'"
  sh """
     touch /tmp/${filePrefix}-all.log
     touch /tmp/${filePrefix}-stdout.log
     touch /tmp/${filePrefix}-stderr.log
     touch /tmp/${filePrefix}-statuscode

     tail -f /tmp/$filePrefix-all.log &
     TAIL_PID=\$!

     ((${config.script} | tee /tmp/${filePrefix}-stdout.log) 3>&1 1>&2 2>&3 | tee /tmp/${filePrefix}-stderr.log) &> /tmp/${filePrefix}-all.log
     echo "${PIPESTATUS[0]}" > /tmp/${filePrefix}-statuscode
     kill \${TAIL_PID}
     rm /tmp/${filePrefix}-all.log
     """
  response.stdout = sh(
    returnStdout: true,
    script: "set +e; cat /tmp/${filePrefix}-stdout.log; rm /tmp/${filePrefix}-stdout.log"
  ).trim()
  response.stderr = sh(
    returnStdout: true,
    script: "set +e; cat /tmp/${filePrefix}-stderr.log; rm /tmp/${filePrefix}-stderr.log"
  ).trim()
  response.statusCode = sh(
    returnStdout: true,
    script: "set +e; cat /tmp/${filePrefix}-statuscode; rm /tmp/${filePrefix}-statuscode"
  ).trim().toInteger()

  return response
}
