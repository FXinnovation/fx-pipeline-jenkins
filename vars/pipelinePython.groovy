def call(Map config = [:], Map closures = [:]) {
    for (closure in closures) {
        if (!closure.value instanceof Closure) {
            error("${closure.key} has to be a java.lang.Closure.")
        }
    }
    mapAttributeCheck(config, 'version', CharSequence, '3')
    // in this array we'll place the jobs that we wish to run

    def branches = [:]

    branches["Unit Tests"] = {
        fxSingleJob([
                pipeline: { Map scmInfo ->
                    def isTagged = '' != scmInfo.tag
                    def MakefileFileExists = fileExists 'Makefile'
                    def toDeploy = false

                    if (isTagged && MakefileFileExists && jobInfo.isManuallyTriggered()) {
                        toDeploy = true
                    }

                    printDebug("isTagged: ${isTagged} | MakefileFileExists: ${MakefileFileExists} | manuallyTriggered: ${jobInfo.isManuallyTriggered()} | toDeploy:${toDeploy}")

                    stage('Unit Tests') {
                        virtualenv(config, closures)
                        test(config, closures)
                    }

                }
        ])

    }

    branches["Lint"] = {
        fxSingleJob([
                pipeline: { Map scmInfo ->
                    def isTagged = '' != scmInfo.tag
                    def MakefileFileExists = fileExists 'Makefile'
                    def toDeploy = false

                    if (isTagged && MakefileFileExists && jobInfo.isManuallyTriggered()) {
                        toDeploy = true
                    }

                    printDebug("isTagged: ${isTagged} | MakefileFileExists: ${MakefileFileExists} | manuallyTriggered: ${jobInfo.isManuallyTriggered()} | toDeploy:${toDeploy}")
                    stage('lint') {
                        virtualenv(config, closures)
                        lint(config, closures)
                    }
                }
        ])


    }

    parallel branches


//  stage('coverage') {
//    virtualenv(config, closures)
//    coverage(config, closures)
//  }

//  publish(config, closures)
}

def virtualenv(Map config = [:], Map closures = [:]) {
    mapAttributeCheck(config, 'version', CharSequence, '3')
    if (!closures.containsKey('virtualenv')) {
        closures.virtualenv = {
            python.virtualenv([
                    version: config.version
            ])
        }
    }
    closures.virtualenv()
}


def test(Map config = [:], Map closures = [:]) {
    mapAttributeCheck(config, 'version', CharSequence, '3')
    if (!closures.containsKey('test')) {
        closures.test = {
            python.test([
                    version: config.version
            ])
        }
    }
    try {
        replay = closures.test()
        if (replay.stdout =~ /FAILED \(errors=.*\)/) {
            error('Some tests have not passed.')
        }
    } catch (error) {
        throw (error)
    } finally {
        junit '**/Reports/*.xml'
    }
}

def lint(Map config = [:], Map closures = [:]) {
    mapAttributeCheck(config, 'version', CharSequence, '3')
    if (!closures.containsKey('lint')) {
        closures.lint = {
            python.lint([
                    version: config.version,
            ])
        }
    }

    closures.lint()

}


