def call(Map config = [:]){
  if (!config.containsKey('script') || !(config.script instanceof CharSequence)){
    error('"script" parameter is mandatory and must be a String (implements CharSequence).')
  } 
  if (!config.containsKey('throwError') || !(config.throwError instanceof Boolean)){
    config.throwError = true
  }

  def filePrefix = UUID.randomUUID().toString()
  def response = [
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
         """
    response.stdout = readFile("/tmp/${filePrefix}-stdout.log").trim()
    response.stderr = readFile("/tmp/${filePrefix}-stderr.log").trim()
    response.statusCode = readFile("/tmp/${filePrefix}-statuscode").trim().toInteger()

    if (config.throwError == true && response.statusCode != 0){
      error(response.stderr)
    }
    return response
  }catch(error){
      throw error
  }finally{
    new File("/tmp/${filePrefix}-stdout.log").delete()
    new File("/tmp/${filePrefix}-stderr.log").delete()
    new File("/tmp/${filePrefix}-statuscode").delete()
  }
}
