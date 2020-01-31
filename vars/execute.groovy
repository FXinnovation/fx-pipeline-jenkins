import com.fxinnovation.io.Debugger

def call(Map config = [:]){
  mapAttributeCheck(config, 'script', CharSequence, '', '"script" parameter is mandatory and must be a String (implements CharSequence).')
  mapAttributeCheck(config, 'throwError', Boolean, true)
  mapAttributeCheck(config, 'printCommand', Boolean, true)
  mapAttributeCheck(config, 'hideStdout', Boolean, false)

  def debugger = new Debugger(this)
  def filePrefix = UUID.randomUUID().toString()
  def response = [
    stdout: null,
    stderr: null,
    statusCode: null
  ]
  if (config.printCommand || debugger.debugVarExists()) {
    println "Executing: '${config.script}'"
  }
  try{
    sh(
      returnStdout: config.hideStdout || debugger.debugVarExists(),
      script: """
              set +x
              mkdir -p /tmp/${filePrefix}
              > /tmp/${filePrefix}/stdout.log
              > /tmp/${filePrefix}/stderr.log
              > /tmp/${filePrefix}/statuscode
              tail -f /tmp/${filePrefix}/stdout.log &
              STDOUT_PID=\$!
              tail -f /tmp/${filePrefix}/stderr.log &
              STDERR_PID=\$!
              set +e
              ${config.script} >> /tmp/${filePrefix}/stdout.log 2>> /tmp/${filePrefix}/stderr.log
              echo \$? > /tmp/${filePrefix}/statuscode
              set -e
              # This sleep is needed to make sure both stdout and stderr have been outputed on jenkins... :(
              sleep 1
              kill \${STDOUT_PID} &> /dev/null
              kill \${STDERR_PID} &> /dev/null
              """
    )

    response.stdout = readFile("/tmp/${filePrefix}/stdout.log").trim()
    response.stderr = readFile("/tmp/${filePrefix}/stderr.log").trim()
    response.statusCode = readFile("/tmp/${filePrefix}/statuscode").trim().toInteger()

    if (config.throwError == true && response.statusCode != 0){
      error("Script returned an error:\nStatus Code: ${response.statusCode}\nStderr:\n${response.stderr}")
    }
    printDebug("### DEBUG ###\n${response}")
    return response
  }catch(error){
    printDebug("### DEBUG ###\n${response}")
    throw error
  }finally{
    dir("/tmp/${filePrefix}"){
      deleteDir()
    }
  }
}
