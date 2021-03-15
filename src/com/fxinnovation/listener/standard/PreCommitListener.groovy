package com.fxinnovation.listener.standard

import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.event_data.PipelineEventData
import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

/**
 * Runs pre-commit hooks. Any fail on one hook will stop the pipeline.
 */
class PreCommitListener extends EventListener {
  private Script context
  private Debugger debugger
  private OptionStringFactory optionStringFactory

  PreCommitListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return PipelineEvents.PRE_TEST
  }

  /**
   * @param PipelineEventData eventData
   * @return PipelineEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    if (!this.shouldRun(eventData)) {
      return eventData
    }

    this.context.execute(script: this.context.dockerRunCommand(
      dockerImage: eventData.getPreCommitDockerImageName(),
      fallbackCommand: 'pre-commit',
      command: 'run -a --color=always',
      dataIsCurrentDirectory: eventData.dockerDataIsCurrentDirectory(),
      dataBasepath: eventData.getDockerDataBasepath(),
    ))

    return eventData
  }

  private Boolean shouldRun(PipelineEventData eventData) {
    return (
      this.context.fileExists('.pre-commit-config.yaml') ||
        this.context.fileExists('.pre-commit-config.yml')
    )
  }
}
