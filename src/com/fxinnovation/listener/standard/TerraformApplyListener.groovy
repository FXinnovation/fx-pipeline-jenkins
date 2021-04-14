package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformApplyListener extends EventListener {
  private Script context

  TerraformApplyListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.APPLY
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    this.context.ansiColor('xterm') {
      this.context.terraform.apply([
          stateOut: eventData.getTestStateFileName(),
          commandTarget: eventData.getPlanOutFile(),
        ] + eventData.getExtraOptions()
      )
    }
    return eventData
  }
}
