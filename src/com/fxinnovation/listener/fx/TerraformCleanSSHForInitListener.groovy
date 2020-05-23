package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.io.Debugger

class TerraformCleanSSHForInitListener extends EventListener {
  private Script context
  private Debugger debugger

  TerraformPrepareSSHForInitListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return TerraformEvents.POST_INIT
  }

  /**
   * @param FXTerraformInitEventData eventData
   * @return FXTerraformInitEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    if (!this.shouldRun(eventData)) {
      this.debugger.printDebug("Skip ${this.getClass()}: no SSH credentials passed as data.")
      return eventData
    }

    this.context.sh('rm -rf ~/.ssh')

    return eventData
  }

  private shouldRun(EventDataInterface eventData = null) {
    return (
      null != eventData.getExtraData().initSSHCredentialId &&
      null != eventData.getExtraData().initSSHHostKeys
    )
  }
}
