package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener
import com.fxinnovation.event_data.TerraformEventData

class TerraformPrepareSSHForInitListener extends EventListener {
  private Script context

  TerraformPrepareSSHForInitListener(Script context) {
    this.context = context
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
      return eventData
    }

    this.context.withCredentials([
        this.context.sshUserPrivateKey(
          credentialsId: eventData.getExtraOptions().initSSHCredentialId,
          keyFileVariable: 'keyFile',
          passphraseVariable: 'passphrase',
          usernameVariable: 'username'
        )
    ]) {
      this.context.sh("cat ${keyFile} > ${this.getSSHKeyFileName(keyFile)}")
      this.context.sh('echo "' + eventData.getExtraData().initSSHHostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')

      this.context.println(eventData.getExtraOptions())

      eventData.setExtraOptions(this.additionJoin(
          eventData.getExtraOptions(),
          [
            dockerAdditionalMounts: [
                '~/.ssh/': '/root/.ssh/',
            ],
            backendConfigs: fileExists('deploy.tf') ? eventData.getExtraData().terraformInitBackendConfigsPublish : eventData.getExtraData().terraformInitBackendConfigsTest
          ]
        )
      )

      this.context.println(eventData.getExtraOptions())

    }

    return eventData
  }

  private String getSSHKeyFileName(String keyFile) {
    return "~/.ssh/id_" + this.context.execute(script: "ssh-keygen -l -f ${keyFile} | rev | cut -d ' ' -f 1 | rev | tr -d ')(' | tr '[:upper:]' '[:lower:]'")
  }

  private shouldRun(EventDataInterface eventData = null) {
    return (
      null != eventData.getExtraData().initSSHCredentialId &&
      null != eventData.getExtraData().initSSHHostKeys
    )
  }

  // TODO: https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/pipeline-jenkins/issues/54
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
