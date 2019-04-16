def call(Map config = [:], Map closures = [:]) {
    for (closure in closures) {
        if (!closure.value instanceof Closure) {
            error("${closure.key} has to be a java.lang.Closure.")
        }
    }
    mapAttributeCheck(config, 'version', CharSequence, '3')
    mapAttributeCheck(config, 'artifacts', CharSequence, '')

    def branches = [:]

    branches['Lint'] = {
        stage('Lint') {
            lint(config, closures)
        }
    }
    branches['Unittest'] = {
        stage('Unittest') {
            test(config, closures)
        }
    }
    branches['Coverage'] = {
        stage('Coverage') {
            coverage(config, closures)
        }
    }

    stage('Virtual Env') {
        virtualenv(config, closures)
    }

    parallel branches

    stage('Build') {
        build(config, closures)
    }

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

def coverage(Map config = [:], Map closures = [:]) {
    mapAttributeCheck(config, 'version', CharSequence, '3')
    mapAttributeCheck(config, 'source', CharSequence, 'source', )
    if (!closures.containsKey('coverage')) {
        closures.coverage = {
            python.coverage([
                    version: config.version
            ])
        }
    }
    try {
        closures.coverage()

    } catch (error) {
        throw (error)
    } finally {
        cobertura(
                autoUpdateHealth: false,
                autoUpdateStability: false,
                coberturaReportFile: 'coverage.xml',
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
}

def build(Map config = [:], Map closures = [:]) {
    mapAttributeCheck(config, 'version', CharSequence, '3')
    mapAttributeCheck(config, 'artifacts', CharSequence, '')

    if (!closures.containsKey('test')) {
        closures.build = {
            python.build([
                    version: config.version
            ])
        }
    }
    try {
        closures.build()
    } catch (error) {
        throw (error)
    } finally {
        if (config.containsKey('artifacts')) {
            archiveArtifacts artifacts: "${config.artifacts}"
        }
    }
}