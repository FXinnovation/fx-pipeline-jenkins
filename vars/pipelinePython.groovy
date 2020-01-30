import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]) {
  mapAttributeCheck(config, 'version', CharSequence, '3')

  closureHelper = new ClosureHelper(this, closures)

  stage('Unit Tests') {
      virtualenv(config, closureHelper)
      test(config, closureHelper)
  }

  stage('lint') {
      virtualenv(config, closureHelper)
      lint(config, closureHelper)
  }
}

static
def virtualenv(Map config = [:], ClosureHelper closureHelper) {
  closureHelper.addClosureOnlyIfNotDefined('virtualenv', {
      python.virtualenv([
              version: config.version
      ])
    }
  )
  closureHelper.execute('virtualenv')
}

static
def test(Map config = [:], ClosureHelper closureHelper) {
  closureHelper.addClosureOnlyIfNotDefined('test', {
      python.test([
        version: config.version
      ])
    }
  )
  closureHelper.execute('test')
}

def lint(Map config = [:], ClosureHelper closureHelper) {
  closureHelper.addClosureOnlyIfNotDefined('lint', {
      python.lint([
        version: config.version,
        folder : folder
      ])
    }
  )
  closureHelper.execute('lint')
}
