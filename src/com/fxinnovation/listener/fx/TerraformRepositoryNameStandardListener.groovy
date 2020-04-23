package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformRepositoryNameStandardListener extends EventListener {
  public static final VALID_DEPLOYMENT_REPOSITORY_NAME_PATTERN = /^terraform\-deployment\-[a-z\d-]{3,}$/
  public static final VALID_MODULE_REPOSITORY_NAME_PATTERN = /^terraform\-(module|ecosystem)\-(aws|azurerm|azuread|google|bitbucket|gitlab|github|kubernetes|multi)-[a-z\d]{2,}([a-z\d\-]+)?[a-z\d]$/

  private Script context

  TerraformRepositoryNameStandardListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.PRE_PIPELINE
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    return this.doRun(eventData)
  }

  private TerraformEventData doRun(TerraformEventData eventData) {
    if (this.isCurrentCodeTerraformDeployment()) {
      if (!(eventData.getScmInfo().getRepositoryName() ==~ this.VALID_DEPLOYMENT_REPOSITORY_NAME_PATTERN)) {
        throw new Exception("This build does not meet FX standards: a Terraform deployment MUST follow this pattern: “${this.VALID_DEPLOYMENT_REPOSITORY_NAME_PATTERN}”. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#repositories.")
      }
    } else {
      if (!(eventData.getScmInfo().getRepositoryName() ==~ this.VALID_MODULE_REPOSITORY_NAME_PATTERN)) {
        throw new Exception("This build does not meet FX standards: a Terraform module MUST follow this pattern: “${this.VALID_MODULE_REPOSITORY_NAME_PATTERN}”. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#repositories.")
      }
    }

    return eventData
  }

  private Boolean isCurrentCodeTerraformDeployment() {
    return this.context.fileExists('deploy.tf')
  }
}
