def call(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')

  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a java.lang.Closure.")
    }
  }

  stage('test “' + config.commandTarget + '”') {
    lint(config, closures)
    galaxy(config, closures)
    converge(config, closures)
    test(config, closures)
  }

  publish(config, closures)
}


def lint(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'lintOptions',    CharSequence, '-p --parseable-severity')
  mapAttributeCheck(config, 'lintOutputFile', CharSequence, 'ansible-lint.txt')

  if (!closures.containsKey('lint')){
    closures.lint = {
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
  }

  if (closures.containsKey('preLint')){
    closures.preLint()
  }

  closures.lint()

  if (closures.containsKey('postLint')){
    closures.postLint()
  }
}

def galaxy(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'galaxySSHHostKeys', List,         [])
  mapAttributeCheck(config, 'galaxyAgentSocket', CharSequence, '')
  mapAttributeCheck(config, 'galaxyReqFile',     CharSequence, 'requirements.yml')
  mapAttributeCheck(config, 'galaxyRolesPath',   CharSequence, 'roles/')

  if (!closures.containsKey('galaxy')){
    closures.galaxy = {
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
  }

  if (closures.containsKey('preGalaxy')){
    closures.preGalaxy()
  }

  closures.galaxy()

  if (closures.containsKey('postGalaxy')){
    closures.postGalaxy()
  }
}

def converge(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'convergeOptions', Map, [:])

  if (!closures.containsKey('converge')){
    closures.converge = {
      println('No Ansible “converge” step define yet.')
    }
  }

  if (closures.containsKey('preConverge')){
    closures.preConverge()
  }

  closures.converge()

  if (closures.containsKey('postConverge')){
    closures.postConverge()
  }
}

def test(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'testOptions', Map, [:])

  if (!closures.containsKey('test')){
    closures.converge = {
      println('No Ansible “test” step define yet.')
    }
  }

  if (closures.containsKey('preTest')){
    closures.preTest()
  }

  closures.test()

  if (closures.containsKey('postTest')){
    closures.postTest()
  }
}

def publish(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'publish', Boolean, false)

  if (config.publish) {
    stage('publish') {
      if (closures.containsKey('prePublish')) {
        closures.prePublish()
      }

      closures.publish()

      if (closures.containsKey('postPublish')) {
        closures.postPublish()
      }
    }
  }else{
    println('Publish step is skipped because "config.publish" is false.')
  }
}
