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
class CheckoutListener extends EventListener {
  private Script context
  private Debugger debugger

  CheckoutListener(Script context, Debugger debugger) {
    this.context = context
    this.debugger = debugger
  }

  @Override
  String listenTo() {
    return PipelineEvents.PREPARE
  }

  /**
   * @param PipelineEventData eventData
   * @return PipelineEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    if (this.shouldCheckoutWithTag(eventData)) {
      this.checkoutWithTag(eventData)
    } else {
      this.context.checkout(this.context.scm)
    }

    def scmInfo = new ScmInfo(
      this.getCommitId(),
      this.getLastCommitId(eventData),
      this.getBranch(),
      this.getDefaultBranch(eventData),
      this.getTag(),
      this.getLatestTag(),
      this.getRepositoryName(this.context.scm)
    )

    this.debugger.printDebug(this.context.sh('ls'))
    this.debugger.printDebug(scmInfo.toString())

    IOC.registerSingleton(ScmInfo.class.getName(), {
      return scmInfo
    })

    return eventData
  }

  private shouldCheckoutWithTag(PipelineEventData eventData) {
    return '' != eventData.getCheckoutTag()
  }

  private checkoutWithTag(PipelineEventData eventData) {
    this.context.dir(eventData.getCheckoutDirectory()) {
      this.context.git(
        credentialsId: eventData.getCheckoutCredentialID(),
        changelog: false,
        poll: false,
        url: eventData.getCheckoutRepositoryURL()
      )

      def tagExist = this.context.execute(
        script: "git rev-parse -q --verify “refs/tags/${eventData.getCheckoutTag()}”",
        throwError: false
      )

      if ('' == tagExist.stdout) {
        throw new Exception("There is no tag “${eventData.getCheckoutTag()}” in the repo “${eventData.getCheckoutRepositoryURL()}“")
      }

      this.context.execute (
        script: "git checkout ${eventData.getCheckoutTag()}"
      )
    }
  }

  private String getCommitId() {
    return executeCommand('git rev-parse HEAD')
  }

  private String getLastCommitId(PipelineEventData eventData) {
    return executeCommand('git rev-parse origin/' + this.getDefaultBranch(eventData))
  }

  private String getBranch() {
    def branch = executeCommand('echo "${BRANCH_NAME}"')
    if ('' == branch) {
      branch = executeCommand('git rev-parse --abbrev-ref HEAD')
    }

    return branch
  }

  private String getTag() {
    return executeCommand('git describe --tags --exact-match')
  }

  private String getLatestTag() {
    return executeCommand('git describe --tags $(git rev-list --tags --max-count=1)')
  }

  private String getRepositoryName(SCM scm) {
    if (scm.metaClass.respondsTo(scm, 'getUserRemoteConfigs')) {
      // Only works on specific implementation of the SCM class, forcing us to check if the method exists
      return scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
    } else {
      // Fallback command to attempt to get repository. Less robust than the scm command above, thus not default.
      def repository = executeCommand('basename -s .git `git config --get remote.origin.url`')

      if ('' == repository) {
        // Last attempt to get the repository name. Most unsure method, thus attempted last.
        repository = executeCommand('basename `git rev-parse --show-toplevel`')
      }

      return repository
    }
  }

  private String getDefaultBranch(PipelineEventData eventData) {
    def defaultBranch = executeCommand('git symbolic-ref --short HEAD')

    if ('' == defaultBranch) {
      // This is not robust
      // However Jenkins is unable to get HEAD pointer on remote, thus making it hard to get default branch
      return eventData.getDefaultBranchName()
    }

    return defaultBranch
  }

  private String executeCommand(String command) {
    try {
      return this.context.execute(script: command, hideStdout: true).stdout.trim()
    } catch (error) {
      this.debugger.printDebug(error)
      return ''
    }
  }

  Integer getOrder() {
    return 10
  }
}
