def call(Map config = [:]){
  if (!config.containsKey('script') || !(config.script instanceof CharSequence)){
    error('"script" parameter is mandatory and must be a String (implements CharSequence).')
  } 
  if (!config.containsKey('throwError') || !(config.throwError instanceof Boolean)){
    config.throwError = true
  }

  def myOutput = [:]
  randomNumber = Math.abs(new Random().nextInt() % 600) + 1

  myOutput[randomNumber] = Math.abs(new Random().nextInt() % 600) + 1

  response = [
    stdout: null,
    stderr: null,
    statusCode: null
  ]
  println "Executing: '${config.script}'"
  try{
      sh """
         set +x
         echo "" > /tmp/${myOutput[randomNumber]}-stdout.log
         echo "" > /tmp/${myOutput[randomNumber]}-stderr.log
         echo "" > /tmp/${myOutput[randomNumber]}-statuscode
         tail -f /tmp/${myOutput[randomNumber]}-stdout.log &
         STDOUT_PID=\$!
         tail -f /tmp/${myOutput[randomNumber]}-stderr.log &
         STDERR_PID=\$!
         set +e
         ${config.script} >> /tmp/${myOutput[randomNumber]}-stdout.log 2>> /tmp/${myOutput[randomNumber]}-stderr.log
         echo \$? > /tmp/${myOutput[randomNumber]}-statuscode
         set -e
         # This sleep is needed to make sure both stdout and stderr have been outputed on jenkins... :(
         sleep 1
         kill \${STDOUT_PID} &> /dev/null
         kill \${STDERR_PID} &> /dev/null
         """
    response.stdout = sh(
      returnStdout: true,
      script: "set +x; cat /tmp/${myOutput[randomNumber]}-stdout.log"
    ).trim()
    response.stderr = sh(
      returnStdout: true,
      script: "set +x; cat /tmp/${myOutput[randomNumber]}-stderr.log"
    ).trim()
    response.statusCode = sh(
      returnStdout: true,
      script: "set +x; cat /tmp/${myOutput[randomNumber]}-statuscode"
    ).trim().toInteger()

    if (config.throwError == true && response.statusCode != 0){
      error(response.stderr)
    }
    return response
  }catch(error){
      throw error
  }finally{
    sh "rm /tmp/${myOutput[randomNumber]}-*"
  }
}
