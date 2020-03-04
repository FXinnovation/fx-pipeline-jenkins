package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformValidateListener extends EventListener {
  private Script context

  TerraformValidateListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.VALIDATE
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
      this.context.terraform.validate(
        [
          commandTarget: eventData.getCommandTarget()
        ] + eventData.getOptions()
      )
    }catch(validateError){
      this.context.printDebug(validateError)
      this.context.error('Terraform validate command has failed!')
    }

    return eventData
  }
}
