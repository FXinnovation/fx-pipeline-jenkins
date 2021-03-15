package com.fxinnovation.listener.standard

import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.event_data.PipelineEventData
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class DockerLoginListener extends EventListener {
  private Script context
  private Debugger debugger

  DockerLoginListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return PipelineEvents.PREPARE
  }

  /**
   * @param PipelineEventData eventData
   * @return PipelineEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    if (eventData.shouldLoginToDockerRegistry()) {

      printDebug(this.context.credentials(eventData.getDockerRegistryCredentialId()))

      this.context.withCredentials([
        this.context.usernamePassword(
          credentialsId: eventData.getDockerRegistryCredentialId(),
          passwordVariable: 'registryPassword',
          usernameVariable: 'registryUsername'
        )
      ]) {
          DOCKER_REGISTRY_USERNAME = credentials('example-credentials-id')
        }

        this.context.execute(
          script: "echo '${registryPassword}' | docker login --username \$registryUsername --password-stdin ${eventData.getDockerRegistry()}",
        )
      }
    }
  }

  private checkDockerRegistry(PipelineEventData eventData = null) {
    if ('' == eventData.getDockerRegistryCredentialId()) {
      throw new Exception('Pipeline was configured to login to docker registry but “registryCredentialId” is empty.')
    }
  }

  Integer getOrder() {
    return 100
  }
}
