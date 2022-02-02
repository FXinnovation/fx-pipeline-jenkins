package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformPrepareGithubForInitListener extends EventListener {
  private Script context
  private Debugger debugger

    TerraformPrepareGithubForInitListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return TerraformEvents.PRE_INIT
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
      this.debugger.printDebug("Skip ${this.getClass()}: no Github credentials passed as data.")
      return eventData
    }

    this.context.withCredentials([
      this.context.usernamePassword(
        credentialsId: eventData.getExtraData().initGithubCredentialId,
        passwordVariable: 'github_password',
        usernameVariable: 'github_username'
      )
    ]) {
      this.context.sh('''
        ssh-keyscan -t rsa github.com >> ~/.ssh/known_hosts
        git config --global credential.helper "store --file ~/.ssh/.credentials"
      ''')
      this.context.sh('echo "https://' + this.context.github_username + ':' + this.context.github_password + '@github.com" >> ~/.ssh/.credentials')
    }

    return eventData
  }

  private shouldRun(EventDataInterface eventData = null) {
    return (
      null != eventData.getExtraData().initGithubCredentialId
    )
  }
}
