import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')

  closureHelper = new ClosureHelper(this, closures)

  stage('test “' + config.commandTarget + '”') {
    lint(config, closureHelper)
    galaxy(config, closureHelper)
    converge(config, closureHelper)
    test(config, closureHelper)
  }

  publish(config, closureHelper)
}


def lint(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'lintOptions',    CharSequence, '-p --parseable-severity')
  mapAttributeCheck(config, 'lintOutputFile', CharSequence, 'ansible-lint.txt')

  closureHelper.addClosureOnlyIfNotDefined('lint', {
    try {
       ansibleLint(
         options: config.lintOptions,
         commandTarget: config.commandTarget
       )
     } catch(error) {
       writeFile(
         file: config.lintOutputFile,
         text: error.getMessage()
       )
       archiveArtifacts(
         artifacts: config.lintOutputFile
       )

       throw(error)
     }
    }
  )

  closureHelper.execute('preLint')
  closureHelper.execute('lint')
  closureHelper.execute('postLint')
}

def galaxy(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'galaxySSHHostKeys', List, [])
  mapAttributeCheck(config, 'galaxyAgentSocket', CharSequence, '')
  mapAttributeCheck(config, 'galaxyReqFile', CharSequence, 'requirements.yml')
  mapAttributeCheck(config, 'galaxyRolesPath', CharSequence, 'roles/')

  closureHelper.addClosureOnlyIfNotDefined('galaxy', {
      if (fileExists(config.galaxyReqFile)) {
        ansibleGalaxy.install([
          sshHostKeys:    config.galaxySSHHostKeys,
          sshAgentSocket: config.galaxyAgentSocket,
          reqFile:        config.galaxyReqFile,
          rolesPath:      config.galaxyRolesPath
        ])
      } else {
        print "The requirement file doesn't exist : ${config.galaxyReqFile}, skip"
      }
    }
  )

  closureHelper.execute('preGalaxy')
  closureHelper.execute('galaxy')
  closureHelper.execute('postGalaxy')
}

def converge(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'convergeOptions', Map, [:])

  closureHelper.addClosureOnlyIfNotDefined('converge', {
      println('No Ansible “converge” step define yet.')
    }
  )

  closureHelper.execute('preConverge')
  closureHelper.execute('converge')
  closureHelper.execute('postConverge')
}

def test(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'testOptions', Map, [:])

  closureHelper.addClosureOnlyIfNotDefined('test', {
      println('No Ansible “test” step define yet.')
    }
  )

  closureHelper.execute('preTest')
  closureHelper.execute('test')
  closureHelper.execute('postTest')
}

def publish(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'publish', Boolean, false)

  closureHelper.addClosureOnlyIfNotDefined('publish', {
      println('No Ansible “publish” step define yet.')
    }
  )

  closureHelper.execute('prePublish')
  closureHelper.execute('publish')
  closureHelper.execute('postPublish')
}
