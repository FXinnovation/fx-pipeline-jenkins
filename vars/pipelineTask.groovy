def call(Map config = [:], Map closures = [:]){
  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a java.lang.Closure.")
    }
  }

  stage('target selection') {
    selectTarget(config, closures)
  }

  stage('command options selection') {
    commandOptions = selectCommandOptions(config, closures)
  }

  stage('command execution') {
    executeCommand(commandOptions, closures)
  }
}

private void selectTarget(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'selectTargetOptions', Map, [:])

  if (!closures.containsKey('selectTarget')){
    closures.selectTarget = {
      println('“selectTarget” step is not defined. Skipping.')
    }
  }

  closures.selectTarget()
}

private Map selectCommandOptions(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'selectCommandOptionsOptions', Map, [:])

  if (!closures.containsKey('selectCommandOptions')){
    closures.selectCommandOptions = {
      println('“selectCommandOptions” step is not defined. Skipping.')
    }
  }

  return closures.selectCommandOptions()
}


private void executeCommand(Map commandOptions, Map closures = [:]){
  if (!closures.containsKey('executeCommand')){
    closures.executeCommand = {
      println('“executeCommand” step is not defined. Skipping.')
    }
  }

  closures.executeCommand()
}