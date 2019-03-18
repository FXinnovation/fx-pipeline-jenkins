def call() {
  fxJob([
    pipeline: {
      try {
        ansiblelint()
      } catch(error) {
        writeFile(
          file: 'ansible-lint.txt',
          text: error.getMessage()
        )
        throw(error)
      }
    },
    preNotify: {
      def issues = scanForIssues blameDisabled: true, tool: pyLint(name: 'ansible-lint', pattern: 'ansible-lint.txt')
      publishIssues issues: [issues]
    }
  ])
}
