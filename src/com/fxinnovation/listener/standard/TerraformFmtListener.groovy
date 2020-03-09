package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.listener.fx.TerraformFileStandardListener
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener
import com.fxinnovation.observer.EventSubscriber

class TerraformFmtListener extends EventSubscriber {
  private Script context

  TerraformFmtListener(Script context) {
    this.context = context
  }

  @Override
  List<String> getSubscribedEvents() {
    return [
      TerraformEvents.FMT,
      TerraformEvents.PRE_PIPELINE
    ]
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
