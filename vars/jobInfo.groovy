Boolean isManuallyTriggered(){
  for (cause in currentBuild.getBuildCauses()) {
    if (cause.containsKey('userId')) {
      return true
    }
  }
  return false
}
