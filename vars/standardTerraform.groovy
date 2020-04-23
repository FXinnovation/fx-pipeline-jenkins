import com.fxinnovation.data.ScmInfo
import com.fxinnovation.di.IOC
import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.helper.ClosureHelper
import com.fxinnovation.observer.EventDispatcher

def call(Map config = [:], Map closures = [:]) {
  fxRegisterListeners()

  mapAttributeCheck(config, 'testPlanVars', List, [])
  mapAttributeCheck(config, 'validateVars', List, [])
  mapAttributeCheck(config, 'publishPlanVars', List, [])
  mapAttributeCheck(config, 'extraData', Map, [:])
  mapAttributeCheck(config, 'commonOptions', Map, [:])
  mapAttributeCheck(config, 'runKind', Boolean, false)
  mapAttributeCheck(config, 'kindCreationTimeout', Integer, 600)
  mapAttributeCheck(config.commonOptions, 'dockerAdditionalMounts', Map, [:])

  closureHelper = new ClosureHelper(this, closures)

  if(config.runKind) {
    config.podVolumes = [
      hostPathVolume(mountPath: '/lib/modules', hostPath: '/lib/modules'),
      hostPathVolume(mountPath: '/sys/fs/cgroup', hostPath: '/sys/fs/cgroup'),
      emptyDirVolume(mountPath: '/root/.kube', memory: false),
    ]
  }

  closureHelper.addClosure('pipeline', { ScmInfo scmInfo ->
    def deployFileExists = fileExists('deploy.tf')
    def toDeploy = false

    if (scmInfo.isPublishable() && deployFileExists && jobInfo.isManuallyTriggered()){
      toDeploy = true
    }

    printDebug("isPublishable: ${scmInfo.isPublishable()} | deployFileExists: ${deployFileExists} | manuallyTriggered: ${jobInfo.isManuallyTriggered()} | toDeploy: ${toDeploy}")

    commandTargets = []
    try {
      execute(script: "[ -d examples ]")

      for (commandTarget in execute(script: "ls examples | sed -e 's/.*/examples\\/\\0/g'").stdout.split()) {
        commandTargets += commandTarget
      }
    } catch (error) {
      commandTargets = ['.']
    }

    printDebug('commandTargets: ' + commandTargets)

    def kindDockerVolume = [:]
    def terraformNetwork = 'bridge'
    try {
      if(config.runKind) {
        execute(
          script: """
set -e
timeout_count=0
while [ ! -f /root/.kube/config ];
do
  if [ "\$timeout_count" -gt $config.kindCreationTimeout ]; then
    echo "KIND cluster creation timeout."
    exit 1
  fi

  if [[ \$((\$timeout_count % 10)) -eq 0 ]]; then
       echo "Waiting until KIND will be ready... (\$timeout_count s elapsed)"
   fi
  
  sleep 1
  
  timeout_count=\$(( \$timeout_count + 1 ))
done
"""       )

        kindDockerVolume = [
          '/root/.kube':'/root/.kube',
        ]

        terraformNetwork = 'host'
      }

      def dockerAdditionalMounts = ['dockerAdditionalMounts': (kindDockerVolume + config.commonOptions.dockerAdditionalMounts)]
      def dockerNetwork = ['dockerNetwork': terraformNetwork]

      def EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())
      def TerraformEventData terraformEventData = new TerraformEventData(config.commandTarget)
      terraformEventData.setScmInfo(scmInfo)
      terraformEventData.setExtraData(config.extraData)

      terraformEventData = eventDispatcher.dispatch(TerraformEvents.PRE_PIPELINE, terraformEventData)
      for(commandTarget in commandTargets) {
        pipelineTerraform(
          config +
            [
              commandTarget     : commandTarget,
              testPlanOptions   : [
                vars: config.testPlanVars,
              ] + config.commonOptions + dockerAdditionalMounts + dockerNetwork,
              testApplyOptions : config.commonOptions + dockerAdditionalMounts + dockerNetwork,
              fmtOptions: config.commonOptions,
              validateOptions   : [
                vars: config.validateVars
              ] + config.commonOptions,
              testDestroyOptions: [
                vars: config.testPlanVars,
              ] + config.commonOptions + dockerAdditionalMounts + dockerNetwork,
              publish           : deployFileExists,
            ], [
          publish    : { publish(config, commandTarget, toDeploy, deployFileExists, closureHelper.getClosures()) }
        ]
        )
      }
      terraformEventData = eventDispatcher.dispatch(TerraformEvents.POST_PIPELINE, terraformEventData)
    } catch(error) {
      throw new Exception(error)
    }
  }
  )

  fxJob(
    closureHelper.getClosures(),
    [
      disableConcurrentBuilds()
    ],
    config
  )
}

private publish(Map config = [:], CharSequence commandTarget, Boolean toDeploy, Boolean deployFileExists, Map closures = [:]) {
  plan = terraform.plan([
    commandTarget: commandTarget,
    out: 'plan.out',
    vars: config.publishPlanVars
  ] + config.commonOptions)

  if (deployFileExists) {
    terraform.show([
      commandTarget: 'plan.out',
    ] + config.commonOptions)
  }

  if (plan.stdout =~ /.*Infrastructure is up-to-date.*/) {
    println('The “plan” does not contain new changes. Infrastructure is up-to-date.')
    return
  }

  if (!toDeploy) {
    println('The code is either not tagged or the pipeline was triggered automatically. Skipping deployment.')
    return
  }

  if (closures.containsKey("notification")) {
    closures.notification('PENDING')
  }
  else {
    fx_notify(
      status: 'PENDING'
    )
  }

  timeout(activity: true, time: 20) {
    foolProofValidation()
  }

  terraform.apply([
    commandTarget: 'plan.out'
  ] + config.commonOptions)
}

