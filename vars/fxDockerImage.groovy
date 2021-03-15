def call(Map config = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'namespace', CharSequence, 'fxinnovation')

  standardDockerImage(config)
}
