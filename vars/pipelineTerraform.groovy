import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.di.IOC
import com.fxinnovation.helper.ClosureHelper
import com.fxinnovation.observer.EventDispatcher
import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData

def call(Map config = [:], Map closures = [:]) {
  registerServices()

  mapAttributeCheck(config, 'publish', Boolean, false)

  def EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())
  def TerraformEventData terraformEventData = new TerraformEventData(config.commandTarget)
  def DeprecatedFunction deprecatedFunction = IOC.get(DeprecatedFunction.class.getName())

  // DEPRECATED - To be removed and not useful after '01-05-2020'
  closureHelper = new ClosureHelper(this, closures)

  stage('init “' + config.commandTarget + '”') {
    terraformEventData.setExtraOptions(config.initOptions)
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_INIT, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('preInit')}, 'closureHelper.execute(\'preInit\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component)', '01-12-2020')
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.INIT, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('init')}, 'closureHelper.execute(\'init\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component).', '01-12-2020')
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_INIT, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('postInit')}, 'closureHelper.execute(\'postInit\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component).', '01-12-2020')
  }

  stage('lint “' + config.commandTarget + '”') {
    terraformEventData.setExtraOptions(config.validateOptions)
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_VALIDATE, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('preValidate')}, 'closureHelper.execute(\'preValidate\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component)', '01-12-2020')
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.VALIDATE, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('validate')}, 'closureHelper.execute(\'Validate\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component).', '01-12-2020')
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_VALIDATE, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('postValidate')}, 'closureHelper.execute(\'postValidate\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component).', '01-12-2020')

    terraformEventData.setExtraOptions(config.fmtOptions)
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_FMT, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('preFmt')}, 'closureHelper.execute(\'preFmt\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component)', '01-12-2020')
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.FMT, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('fmt')}, 'closureHelper.execute(\'Fmt\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component).', '01-12-2020')
    terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_FMT, terraformEventData)
    deprecatedFunction.execute({closureHelper.execute('postFmt')}, 'closureHelper.execute(\'postFmt\')', 'observer system (https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/wiki#user-content-observer-component).', '01-12-2020')
  }

  if (!config.publish) {
    stage('test “' + config.commandTarget + '”') {
      try {
        terraformEventData.setExtraOptions(config.testPlanOptions)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_PLAN, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.PLAN, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_PLAN, terraformEventData)

        terraformEventData.setExtraOptions(config.testApplyOptions)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_APPLY, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.APPLY, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_APPLY, terraformEventData)

        terraformEventData.setExtraOptions(config.testPlanOptions)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_PLAN_REPLAY, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.PLAN_REPLAY, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_PLAN_REPLAY, terraformEventData)

        this.inspec(config, closureHelper)

      } catch (errorApply) {
        archiveArtifacts(
          allowEmptyArchive: true,
          artifacts: terraformEventData.getTestStateFileName()
        )
        println(errorApply)
        throw (errorApply)
      } finally {
        terraformEventData.setExtraOptions(config.testDestroyOptions)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_DESTROY, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.DESTROY, terraformEventData)
        terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_DESTROY, terraformEventData)
      }
    }

    println('Publish step is skipped because "config.publish" is false.')
    return
  }

  stage('publish') {
    closureHelper.execute('prePublish')
    closureHelper.execute('publish')
    closureHelper.execute('postPublish')
  }
}

def inspec(Map config = [:], ClosureHelper closureHelper) {
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')

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
  } else {
    println('Did not find inspec tests, skipping them')
  }
}
