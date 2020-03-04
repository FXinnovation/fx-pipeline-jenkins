package com.fxinnovation.listener

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

  @Override
  TerraformEventData run(TerraformEventData eventData = null) {
    this.context.terraform.init([
        commandTarget: eventData.getCommandTargets()[0]
      ] + eventData.getOptions()
    )

    return eventData
  }
}
