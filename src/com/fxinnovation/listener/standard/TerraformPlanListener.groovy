package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformPlanListener extends EventListener {
  private Script context

  TerraformPlanListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.PLAN
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    this.context.terraform.plan([
        out: eventData.getPlanOutFile(),
        state: eventData.getTestStateFileName(),
        commandTarget: eventData.getCommandTarget(),
      ] + eventData.getExtraOptions()
    )

    return eventData
  }
}
