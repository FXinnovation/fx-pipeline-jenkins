package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener
import com.fxinnovation.event_data.TerraformEventData

class TerraformInitListener extends EventListener {
  private Script context

  TerraformInitListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.INIT
  }

  /**
   * @param FXTerraformInitEventData eventData
   * @return FXTerraformInitEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    this.context.sshagent([eventData.getExtraData().initSSHCredentialId]) {
      this.context.sh('ssh-add -l')
      this.context.sh('mkdir -p ~/.ssh')
      this.context.sh('echo "' + eventData.getExtraData().initSSHHostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')
      this.context.terraform.init(this.additionJoin([
        commandTarget: eventData.getCommandTarget(),
        dockerAdditionalMounts: [
          '~/.ssh/': '/root/.ssh/',
          '\$(readlink -f $SSH_AUTH_SOCK)': '/ssh-agent',
        ],
        dockerEnvironmentVariables: [
          'SSH_AUTH_SOCK': '/ssh-agent',
        ],
        backendConfigs: fileExists('deploy.tf') ? eventData.getExtraData().terraformInitBackendConfigsPublish : eventData.getExtraData().terraformInitBackendConfigsTest
      ], eventData.getExtraOptions()))
    }

    return eventData
  }

  Boolean stopPropagationAfterRun() {
    return true;
  }

  Integer getOrder() {
    return 500;
  }

  // TODO: https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/issues/54
  private Map additionJoin(Map firstMap, Map secondMap) {
    secondMap.each { key, value ->
      if( firstMap[key])     {
        firstMap[key] = firstMap[key] + secondMap[key]
      }
      else {
        firstMap[key] = secondMap[key]
      }
    }

    return firstMap
  }
}
