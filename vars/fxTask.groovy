def call(Map config = [:], Map closures = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(closures, 'selectTarget', Closure, {}, 'Please define a “selectTarget” closure.')
  mapAttributeCheck(closures, 'selectCommandOptions', Closure, {}, 'Please define a “selectCommandOptions” closure.')
  mapAttributeCheck(closures, 'executeCommand', Closure, {}, 'Please define a “executeCommand” closure.')

  fxJob([
    pipeline: { Map scmInfo ->
      pipelineTask(config, closures)
    }
  ])
}
