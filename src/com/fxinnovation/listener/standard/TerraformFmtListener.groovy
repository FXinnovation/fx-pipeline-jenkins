package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformFmtListener extends EventListener {
  private Script context

  TerraformFmtListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.FMT
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    try {
      this.context.terraform.fmt([
          check: true,
          commandTarget: eventData.getCommandTarget(),
        ] + eventData.getExtraOptions()
      )
    } catch(fmtError) {
      this.context.printDebug(fmtError)
      this.context.error("ERROR: Terraform fmt command has failed!")
    }

    return eventData
  }
}
