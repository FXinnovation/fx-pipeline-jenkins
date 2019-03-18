def call(Map config = [:]) {
  if (!config.containsKey('ansiblelintConfig')) {
    config.ansiblelintConfig = [:]
  } else if (!(config.ansiblelintConfig instanceof Map)) {
    error('ansiblelintConfig parameter must be of type Map')
  }

  if (!config.containsKey('ansiblelintOutputFile')) {
    config.ansiblelintOutputFile = 'ansible-lint.txt'
  } else if (!(config.ansiblelintOutputFile instanceof CharSequence)) {
    error('ansiblelintOutputFile parameter must be of type CharSequence')
  }

  fxJob([
    pipeline: {
      pipelinePlaybook(config)
    },
    preNotify: {
      def issues = scanForIssues blameDisabled: true, tool: pyLint(name: 'ansible-lint', pattern: config.ansiblelintOutputFile)
      publishIssues issues: [issues]
    }
  ])
}
