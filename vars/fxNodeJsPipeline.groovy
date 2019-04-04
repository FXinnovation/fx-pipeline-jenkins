def call(Map config = [:]){
  fxJob([
    pipeline: {
      Map scmInfo ->
      pipelineNodeJsYarn(
        [:],
        [
          "preTest": {
            yarn(
              subcommand: 'lint'
            )
          }
        ]
      )
    },
    preNotify: {
      junit 'xunit.xml'
      cobertura(
        autoUpdateHealth: false,
        autoUpdateStability: false,
        coberturaReportFile: 'coverage/*',
        conditionalCoverageTargets: '70, 0, 0',
        failUnhealthy: false,
        failUnstable: false,
        lineCoverageTargets: '80, 0, 0',
        maxNumberOfBuilds: 0,
        methodCoverageTargets: '80, 0, 0',
        onlyStable: false,
        sourceEncoding: 'ASCII',
        zoomCoverageChart: false
      )
    }
  ])
}
