package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformDestroyListener extends EventListener {
  private Script context

  TerraformDestroyListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.DESTROY
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    this.context.terraform.destroy([
        state: eventData.getTestStateFileName(),
        commandTarget: eventData.getCommandTarget()
      ] + eventData.getTerraformOptions()
    )

    return eventData
  }
}
