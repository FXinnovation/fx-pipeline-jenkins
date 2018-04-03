def call(String command){
  // Executing command while teeing output to a file
  // NOTE: This is to ensure that the output of the command will
  // be available to us in th build logs
  def failed = false
  try{
    sh(
      returnStdout: false,
      script:       "set -o pipefail && ${command} | tee /tmp/output"
    )
  }catch(error){
    failed = true
  }finally{
    // Fetching output of the command and trim it to remove any blank lines
    // and trailing spaces
    def output = sh(
      returnStdout: true,
      script:       'cat /tmp/output'
    ).trim()
    // Remove temprary file created by us while executing the command
    sh(
      returnStdout: false,
      script:       'rm /tmp/output'
    )
    // If command has an error, throw the output of the command
    if (failed){
      throw output
    }
    // Return the output
    return output
  }
}
