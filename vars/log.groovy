def call(hashMap){
  def config = [:]
  hashMap.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config

  def output  = config.output ?: false
  def message = config.message ?: 'No message given'
  // If output is true
  if (output) {
    // Print message
    println(">>>>> ${message}")
  }
}
