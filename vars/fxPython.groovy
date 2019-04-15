def call(Map config = [:]) {

    mapAttributeCheck(config, 'version', CharSequence, '3')
    mapAttributeCheck(config, 'source', CharSequence, 'source_directory', 'need to provide the directory of the sources.')


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
                        version: config.version,
                        source: config.source
                ])

            }
    ])
}
