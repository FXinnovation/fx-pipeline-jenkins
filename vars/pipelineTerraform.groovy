import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  registerServices()

  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')
  mapAttributeCheck(config, 'fmtOptions', Map, [:])

  closureHelper = new ClosureHelper(this, closures)

  stage('init “' + config.commandTarget + '”') {
    init(config, closureHelper)
  }

  stage('test “' + config.commandTarget + '”') {
    validate(config, closureHelper)
    fmt(config.commandTarget, config.fmtOptions, closureHelper)
    test(config, closureHelper)
  }

  publish(config, closureHelper)
}

def init(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'initOptions', Map, [:])

  closureHelper.addClosureOnlyIfNotDefined('init', {
      terraform.init([
          commandTarget: config.commandTarget
        ] + config.initOptions
      )
    }
  )

  closureHelper.execute('preInit')
  closureHelper.execute('init')
  closureHelper.execute('postInit')
}

def validate(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'validateOptions', Map, [:])

  closureHelper.addClosureOnlyIfNotDefined('validate', {
      try {
        terraform.validate(
          [
            commandTarget: config.commandTarget
          ] + config.validateOptions
        )
      }catch(errValidate){
        printDebug(errValidate)
        error "Terraform validate command has failed!"
      }
    }
  )

  closureHelper.execute('preValidate')
  closureHelper.execute('validate')
  closureHelper.execute('postValidate')
}

def fmt(CharSequence commandTarget = '.', Map fmtOptions = [:], ClosureHelper closureHelper){
  closureHelper.addClosureOnlyIfNotDefined('fmt', {
      try{
        terraform.fmt(
          [
            check: true,
            commandTarget: commandTarget,
          ] + fmtOptions
        )
      }catch(errFmt){
        printDebug(errFmt)
        error "Terraform fmt command has failed!"
      }
    }
  )

  closureHelper.execute('preFmt')
  closureHelper.execute('fmt')
  closureHelper.execute('postFmt')
}

def test(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'testPlanOptions', Map, [:])
  mapAttributeCheck(config, 'testApplyOptions', Map, [:])
  mapAttributeCheck(config, 'testDestroyOptions', Map, [:])
  mapAttributeCheck(config, 'publish', Boolean, false)

  closureHelper.addClosureOnlyIfNotDefined('test', {
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
                "file": "${config.commandTarget}-inspec-results.xml"
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
              testResults: "${config.commandTarget}-inspec-results.xml"
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
  )

  closureHelper.execute('preTest')
  closureHelper.execute('test')
  closureHelper.execute('postTest')
}

def publish(Map config = [:], ClosureHelper closureHelper){
  mapAttributeCheck(config, 'publishPlanOptions', Map, [:])
  mapAttributeCheck(config, 'publishApplyOptions', Map, [:])
  mapAttributeCheck(config, 'publish', Boolean, false)

  if (config.publish) {
    stage('publish') {
      closureHelper.execute('prePublish')
      closureHelper.execute('publish')
      closureHelper.execute('postPublish')
    }
  }else{
    println 'Publish step is skipped because "config.publish" is false.'
  }
}
