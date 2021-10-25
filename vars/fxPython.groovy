import com.fxinnovation.data.ScmInfo

def call(Map config = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'version', CharSequence, '3')

  standardJob([
    pipeline: { ScmInfo scmInfo ->
      def MakefileFileExists = fileExists 'Makefile'
      def toDeploy = false

      if (scmInfo.isPublishable() && MakefileFileExists && jobInfo.isManuallyTriggered()) {
        toDeploy = true
      }

      printDebug("isPublishable: ${scmInfo.isPublishable()} | MakefileFileExists: ${MakefileFileExists} | manuallyTriggered: ${jobInfo.isManuallyTriggered()} | toDeploy:${toDeploy}")

      pipelinePython([
        version: config.version
      ])
    }
  ])
}
