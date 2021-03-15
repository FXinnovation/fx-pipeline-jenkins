package com.fxinnovation.listener.standard

import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.event_data.PipelineEventData
import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

/**
 * Logins to a docker registry
 */
class DockerLoginListener extends EventListener {
  private Script context
  private Debugger debugger
  private OptionStringFactory optionStringFactory

  DockerLoginListener(Script context, Debugger debugger, OptionStringFactory optionStringFactory) {
    this.context = context
    this.debugger = debugger
    this.optionStringFactory = optionStringFactory
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
    if (!this.shouldRun(eventData)) {
      return eventData
    }

    this.context.withCredentials([
      this.context.usernamePassword(
        credentialsId: eventData.getDockerRegistryCredentialId(),
        passwordVariable: 'DOCKER_REGISTRY_PASSWORD',
        usernameVariable: 'DOCKER_REGISTRY_USERNAME'
      )
    ]) {
      this.optionStringFactory.createOptionString(' ')
      this.optionStringFactory.addOption('--username', '$DOCKER_REGISTRY_USERNAME')
      this.optionStringFactory.addOption('--password', '$DOCKER_REGISTRY_PASSWORD')
      this.optionStringFactory.addOption(eventData.getDockerRegistry())

      this.context.execute(
        script: "docker login ${this.optionStringFactory.getOptionString().toString()}",
      )
    }

    return eventData
  }

  private Boolean shouldRun(PipelineEventData eventData) {
    return eventData.shouldLoginToDockerRegistry()
  }

  private checkDockerRegistry(PipelineEventData eventData = null) {
    if ('' == eventData.getDockerRegistryCredentialId()) {
      throw new Exception('Pipeline was configured to login to docker registry but “registryCredentialId” is empty.')
    }
  }
}
