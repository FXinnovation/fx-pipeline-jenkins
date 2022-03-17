package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.io.Debugger

class TerraformPrepareSSHForInitListener extends EventListener {
  private Script context
  private Debugger debugger

  TerraformPrepareSSHForInitListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return TerraformEvents.PRE_INIT
  }

  /**
   * @param FXTerraformInitEventData eventData
   * @return FXTerraformInitEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    if (!this.shouldRun(eventData)) {
      this.debugger.printDebug("Skip ${this.getClass()}: no SSH credentials passed as data.")
      return eventData
    }

    this.context.withCredentials([
      this.context.sshUserPrivateKey(
        credentialsId: eventData.getExtraData().initSSHCredentialId,
        keyFileVariable: 'keyFile',
        passphraseVariable: 'passphrase',
        usernameVariable: 'username'
      )
    ]) {
      this.context.sh('mkdir -p ~/.ssh')
      this.context.sh('cat '+  this.context.keyFile +' > '+ this.getSSHKeyFileName(this.context.keyFile))
      this.context.sh('chmod 600 '+ this.getSSHKeyFileName(this.context.keyFile))
      this.context.sh('echo "' + eventData.getExtraData().initSSHHostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')

      eventData.setExtraOptions(this.additionJoin(
          eventData.getExtraOptions(),
          [
            dockerAdditionalMounts: [
                '~/.ssh/': '/root/.ssh/',
            ],
            backendConfigs: this.context.fileExists('deploy.tf') ? eventData.getExtraData().terraformInitBackendConfigsPublish : eventData.getExtraData().terraformInitBackendConfigsTest
          ]
        )
      )
    }

    return eventData
  }

  private String getSSHKeyFileName(String keyFile) {
    return "~/.ssh/id_" + this.context.execute(script: "ssh-keygen -l -f ${keyFile} | rev | cut -d ' ' -f 1 | rev | tr -d ')(' | tr '[:upper:]' '[:lower:]'").stdout
  }

  private shouldRun(EventDataInterface eventData = null) {
    return (
      null != eventData.getExtraData().initSSHCredentialId &&
      null != eventData.getExtraData().initSSHHostKeys
    )
  }

  // TODO: https://github.com/FXinnovation/fx-pipeline-jenkins/issues/54
  private Map additionJoin(Map firstMap, Map secondMap) {
    secondMap.each { key, value ->
      if(firstMap[key])     {
        firstMap[key] = firstMap[key] + secondMap[key]
      } else {
        firstMap[key] = secondMap[key]
      }
    }

    return firstMap
  }
}
