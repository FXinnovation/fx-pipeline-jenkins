def call(Map config = [:], Map closures = [:]){
  for (closure in closures){
    if (!closure.value instanceof Closure){
      error("${closure.key} has to be a java.lang.Closure.")
    }
  }
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')

  stage('init “' + config.commandTarget+ '”') {
    init(config, closures)
  }

  stage('test “' + config.commandTarget + '”') {
    validate(config, closures)
    test(config, closures)
  }

  publish(config, closures)
}

def init(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'initOptions', Map, [:])

  if (!closures.containsKey('init')){
    closures.init = {
      terraform.init([
          commandTarget: config.commandTarget
        ] + config.initOptions
      )
    }
  }

  if (closures.containsKey('preInit')){
    closures.preInit()
  }

  closures.init()

  if (closures.containsKey('postInit')){
    closures.postInit()
  }
}

def validate(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'validateOptions', Map, [:])
  mapAttributeCheck(config, 'fmtOptions', Map, [:])

  if (!closures.containsKey('validate')){
    closures.validate = {
      terraform.validate([
          commandTarget: config.commandTarget
        ] + config.validateOptions
      )
      try{
        terraform.fmt([
            check: true,
            commandTarget: config.commandTarget,
          ] + config.fmtOptions
        )
      }catch(errFmt){
        error "Terraform fmt command has failed!"
      }
    }
  }

  if (closures.containsKey('preValidate')){
    closures.preValidate()
  }

  closures.validate()

  if (closures.containsKey('postValidate')){
    closures.postValidate()
  }
}

def test(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'testPlanOptions', Map, [:])
  mapAttributeCheck(config, 'testApplyOptions', Map, [:])
  mapAttributeCheck(config, 'testDestroyOptions', Map, [:])
  mapAttributeCheck(config, 'publish', Boolean, false)

  if (!closures.containsKey('test')){
    closures.test = {
      if (config.publish) {
        return
      }

      try {
        terraform.plan([
            out: 'test.out',
            state: 'test.tfstate',
            commandTarget: config.commandTarget,
          ] + config.testPlanOptions
        )
        terraform.apply([
            stateOut: 'test.tfstate',
            commandTarget: 'test.out',
          ] + config.testApplyOptions
        )
        replay = terraform.plan([
            out: 'test.out',
            state: 'test.tfstate',
            commandTarget: config.commandTarget,
          ] + config.testPlanOptions
        )
        if (!(replay.stdout =~ /.*Infrastructure is up-to-date.*/)) {
          error('Replaying the “plan” contains new changes. It is important to make sure terraform consecutive runs make no changes.')
        }
        inspecPresent = fileExists(
          "${config.commandTarget}/inspec.yml"
        )
        if (inspecPresent){
          mapAttributeCheck(config, 'inspecTarget', String, '', 'Please define the inspecTarget')
          mapAttributeCheck(config, 'inspecUsername', String, '', 'Please define the inspecUsername')
          mapAttributeCheck(config, 'inspecPassword', String, '', 'Please define the inspecPassword')
          switch (config.inspecTarget) {
            case 'aws':
              mapAttributeCheck(config, 'inspecRegion', String, '', 'Please define the inspecRegion')
              envVariables = [
                AWS_REGION: config.inspecRegion,
                AWS_ACCESS_KEY_ID: config.inspecUsername,
                AWS_SECRET_KEY_ID: config.inspecPassword,
              ]
              break
            case 'azure':
              mapAttributeCheck(config, 'inspecSubscriptionId', String, '', 'Please define the inspecSubscriptionId')
              mapAttributeCheck(config, 'inspecTenantId', String, '', 'Please define the inspecTenantId')
              envVariables = [
                AZURE_SUBSCRIPTION_ID: config.inspecSubscriptionId,
                AZURE_CLIENT_ID: config.inspecUsername,
                AZURE_CLIENT_SECRET: config.inspecPassword,
                AZURE_TENANT_ID: config.inspecTenantId
              ]
              break
            case 'gcp':
              error('GCP in not supported yet by inspec')
              break
            default:
              error('inspecTarget must be one of (gcp|azure|aws)')
              break
          }
          inspecConfig = """
          {
            "reporter": {
              "cli": {
                "stdout": true
              },
              "junit": {
                "stdout": false,
                "file": "inspec-results.xml"
              }
            }
          }
          """
          writeFile(
            file: 'inspec-config.json',
            text: inspecConfig
          )
          try{
            inspec.exec(
              target: "${config.inspecTarget}://",
              jsonConfig: 'inspec-config.json',
              commandTarget: config.commandTarget,
              dockerEnvironmentVariables: envVariables
            )
          }catch(inspecError){
            throw inspecError
          }finally{
            junit(
              allowEmptyResults: true,
              testResults: 'inspec-results.xml'
            )
          }
        }else{
          println 'Did not find inspec tests, skipping them'
        }
      } catch (errorApply) {
        archiveArtifacts(
          allowEmptyArchive: true,
          artifacts: 'test.tfstat*'
        )
        println(errorApply)
        throw (errorApply)
      } finally {
        terraform.destroy([
            state: 'test.tfstate',
            commandTarget: config.commandTarget
          ] + config.testDestroyOptions
        )
        sh 'rm -f test.tfstate'
        sh 'rm -f test.out'
      }
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
  mapAttributeCheck(config, 'publishPlanOptions', Map, [:])
  mapAttributeCheck(config, 'publishApplyOptions', Map, [:])
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
    println 'Publish step is skipped because "config.publish" is false.'
  }
}
