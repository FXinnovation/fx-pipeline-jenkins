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
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    this.context.println('OKOKOKOK')
    this.context.println(eventData.getCommandTarget())
    this.context.println(eventData.getCommandTarget().getClass())
    this.context.println(eventData.getOptions())
    this.context.println(eventData.getOptions().getClass())

    this.context.terraform.init([
        commandTarget: eventData.getCommandTarget()
      ] + eventData.getExtraOptions()
    )

    return eventData
  }
}
