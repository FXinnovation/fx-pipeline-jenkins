def isManuallyTriggered(){
  currentBuild.getBuildCauses().each { cause ->
    if (cause.containsKey('_class') && cause['_class'] == 'hudson.model.Cause$UserIdCause') {
      return true
    }
  }
  return false
}
