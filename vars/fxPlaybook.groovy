def call(Map config = [:]) {
  mapAttributeCheck(config, 'ansiblelintConfig', Map, [:])
  mapAttributeCheck(config, 'ansiblelintOutputFile', CharSequence, 'ansible-lint.txt')

  fxJob([
    pipeline: {
      pipelinePlaybook(config)
    },
    preNotify: {
      def issues = scanForIssues(
        blameDisabled: true,
        tool: pyLint(
          name: 'ansible-lint',
          pattern: config.ansiblelintOutputFile
        )
      )
      publishIssues issues: [issues]
    }
  ])
}
