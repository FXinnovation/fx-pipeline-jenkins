def call(Map config = [:], Map closures = [:]) {
  mapAttributeCheck(closures, 'selectProject', Closure, {}, 'Please define a “selectProject” closure.')
  mapAttributeCheck(closures, 'selectCommandOptions', Closure, {}, 'Please define a “selectCommandOptions” closure.')
  mapAttributeCheck(closures, 'executeCommand', Closure, {}, 'Please define a “executeCommand” closure.')

  fxJob([
    pipeline: { Map scmInfo ->
      pipelineTask(config, closures)
    }
  ])
}
