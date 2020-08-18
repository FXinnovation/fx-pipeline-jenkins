import com.fxinnovation.data.ScmInfo
import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]) {
  registerServices()

  mapAttributeCheck(config, 'awsNukeConfigFileName', CharSequence, '', 'ERROR: “awsNukeConfigFileName” must be defined!')
  mapAttributeCheck(config, 'autoRunCron', CharSequence, '@midnight')
  mapAttributeCheck(config, 'recreateDefaultVpcResources', Boolean, 'true')

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.addClosure('pipeline', { ScmInfo scmInfo ->
    def additionalMounts = [
      config.awsNukeConfigFileName : '/data/aws_nuke_config.yaml'
    ]

     def isDryRun = true

     if('master' == scmInfo.getBranch()) { 
       isDryRun = false
     }

     dockerRunCommand(
       config: '/data/aws_nuke_config.yaml',
       additionalMounts: additionalMounts,
       forceSleep: 3,
       noDryRun: isDryRun,
       recreateDefaultVpcResources: config.recreateDefaultVpcResources,
     ) + config
  }

  fxJob(
    closureHelper.getClosures(),
    [
      buildDiscarder(
        logRotator(
          artifactDaysToKeepStr: '',
          artifactNumToKeepStr: '10',
          daysToKeepStr: '',
          numToKeepStr: '10'
        )
      ),
      pipelineTriggers(
        [cron(config.autoRunCron)]
      )
    ],
    config
  )
}
