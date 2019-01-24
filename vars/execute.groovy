def call(Map config = [:]){
  if (!config.containsKey('script')){
    error('"script" parameter is mandatory.')
  } 
  if (!config.containsKey('returnDetails') || !config.returnDetails instanceof Boolean ){
    config.returnDetails = true
  }

  // TODO: Handle windows slaves
  filePrefix = new Date().getTime()
  response = [:]
  println config.script
  sh """
  set +x
  set -o pipefail
  touch /tmp/${filePrefix}-all.log
  tail -f /tmp/$filePrefix-all.log &
  TAIL_PID=\$!
  ((${config.script} | tee /tmp/${filePrefix}-stdout.log) 3>&1 1>&2 2>&3 | tee /tmp/${filePrefix}-stderr.log) &> /tmp/${filePrefix}-all.log
  echo \$? > /tmp/${filePrefix}-statuscode
  kill \${TAIL_PID}
  rm /tmp/${filePrefix}-all.log
  """
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
  //bash -c "((${config.script} | tee /tmp/${filePrefix}-stdout.log) 3>&1 1>&2 2>&3 | tee /tmp/${filePrefix}-stderr.log) &> /tmp/${filePrefix}-all.log"
