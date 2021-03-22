package com.fxinnovation.listener.standard

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformPlanReplayListener extends EventListener {
  private Script context

  TerraformPlanReplayListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.PLAN_REPLAY
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    def replay = this.context.terraform.plan([
        out: eventData.getPlanOutFile(),
        state: eventData.getTestStateFileName(),
        commandTarget: eventData.getCommandTarget(),
      ] + eventData.getExtraOptions()
    )

    if (!(replay.stdout =~ /.*(Infrastructure is up-to-date|0 to add, 0 to change, 0 to destroy).*/)) {
      this.context.error('ERROR: Replaying the “plan” contains new changes. It is important to make sure terraform consecutive runs make no changes.')
    }

    return eventData
  }
}
