def call(hashMap = [:]){
  def config = [:]
  hashMap.resolveStrategy = Closure.DELEGATE_FIRST
  hashMap.delegate = config

  def output  = config.output ?: false
  def message = config.message ?: 'No message given'
  if (output == true) {
    println(">>>>> ${message}")
  }
}