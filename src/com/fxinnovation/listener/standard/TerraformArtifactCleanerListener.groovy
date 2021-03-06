package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformArtifactCleanerListener extends EventListener {
  private Script context

  TerraformArtifactCleanerListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.POST_DESTROY
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    this.context.sh('rm -f '+ eventData.getStateFileName())
    this.context.sh('rm -f '+ eventData.getPlanOutFile())

    return eventData
  }
}
