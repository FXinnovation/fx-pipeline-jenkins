package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

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
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    this.context.terraform.init([
        commandTarget: eventData.getCommandTarget()
      ] + eventData.getExtraOptions()
    )

    return eventData
  }
}
