package com.fxinnovation.listener.standard

import com.fxinnovation.data.ScmInfo
import com.fxinnovation.di.IOC
import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.event_data.PipelineEventData
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener
import hudson.scm.SCM

/**
 * Handles SCM checkouts
 */
class MoveFilesListener extends EventListener {
  private Script context
  private Debugger debugger

    MoveFilesListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return PipelineEvents.POST_PREPARE
  }

  /**
   * @param PipelineEventData eventData
   * @return PipelineEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    if (eventData.getWorkingDirectory() != '') {
      this.context.sh('mv ' + eventData.getWorkingDirectory() + '/* .')
      this.context.sh('ls -alrt')
    }
  }

}
