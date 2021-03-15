package com.fxinnovation.listener.standard

import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.event_data.PipelineEventData
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

/**
 * Displays a header message in the outputs at the beginning of the pipeline
 */
class HeaderDisplayerListener extends EventListener {
  private Script context
  private Debugger debugger

  HeaderDisplayerListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return PipelineEvents.PRE_PREPARE
  }

  /**
   * @param PipelineEventData eventData
   * @return PipelineEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    if (!this.shouldRun(eventData)) {
      return eventData
    }

    print(eventData.getHeaderMessage())

    return eventData
  }

  private shouldRun(PipelineEventData eventData) {
    return '' != eventData.getHeaderMessage()
  }
}
