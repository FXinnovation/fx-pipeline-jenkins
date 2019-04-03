def isManuallyTriggered(){
  currentBuild.getBuildCauses().each { cause ->
    print(cause)
    println(cause.containsKey('_class'))
    println(cause['_class'] == 'hudson.model.Cause$UserIdCause')
    if (cause.containsKey('_class') && cause['_class'] == 'hudson.model.Cause$UserIdCause') {
      return true
    }
  }
  return false
}
