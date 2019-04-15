def call(Map config = [:], Map closures = [:]) {
    for (closure in closures) {
        if (!closure.value instanceof Closure) {
            error("${closure.key} has to be a java.lang.Closure.")
        }
    }
    mapAttributeCheck(config, 'version', CharSequence, '3')
    // in this array we'll place the jobs that we wish to run

    stage('Virtual Env') {
        virtualenv(config, closures)
    }


    stage('Lint') {
        lint(config, closures)
    }
    stage('Unittest') {
        test(config, closures)
    }

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

def coverage(Map config = [:], Map closures = [:]) {
    mapAttributeCheck(config, 'version', CharSequence, '3')
    mapAttributeCheck(config, 'source', CharSequence, 'source', )
    if (!closures.containsKey('coverage')) {
        closures.coverage = {
            python.coverage([
                    version: config.version,
                    source: config.source
            ])
        }
    }
    try {
        closures.coverage()

    } catch (error) {
        throw (error)
    } finally {
        cobertura('coverage.xml') {
            failNoReports(true)
            sourceEncoding('ASCII')

            // the following targets are added by default to check the method, line and conditional level coverage
            methodTarget(80, 0, 0)
            lineTarget(80, 0, 0)
            conditionalTarget(70, 0, 0)
        }
    }
}
