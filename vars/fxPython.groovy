def call(Map config = [:]) {

    mapAttributeCheck(config, 'version', CharSequence, '3')

    parallel fxJob([
            pipeline: { Map scmInfo ->
                def isTagged = '' != scmInfo.tag
                def MakefileFileExists = fileExists 'Makefile'
                def toDeploy = false

                if (isTagged && MakefileFileExists && jobInfo.isManuallyTriggered()) {
                    toDeploy = true
                }

                printDebug("isTagged: ${isTagged} | MakefileFileExists: ${MakefileFileExists} | manuallyTriggered: ${jobInfo.isManuallyTriggered()} | toDeploy:${toDeploy}")

                pipelinePython([
                        version: config.version
                        stage: 'lint'
                ])

            }
    ]),
   fxJob([
            pipeline: { Map scmInfo ->
                def isTagged = '' != scmInfo.tag
                def MakefileFileExists = fileExists 'Makefile'
                def toDeploy = false

                if (isTagged && MakefileFileExists && jobInfo.isManuallyTriggered()) {
                    toDeploy = true
                }

                printDebug("isTagged: ${isTagged} | MakefileFileExists: ${MakefileFileExists} | manuallyTriggered: ${jobInfo.isManuallyTriggered()} | toDeploy:${toDeploy}")

                pipelinePython([
                        version: config.version
                        stage: 'test'
                ])

            }
    ])


}
