import com.fxinnovation.data.ScmInfo

def call(Map config = [:]){
  fxRegisterListeners()

  fxJob([
    pipeline: {
      ScmInfo scmInfo ->
      pipelineNodeJsYarn(
        [:],
        [
          "preTest": {
            yarn(
              subCommand: 'lint'
            )
          }
        ]
      )
    },
    preNotification: {
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
