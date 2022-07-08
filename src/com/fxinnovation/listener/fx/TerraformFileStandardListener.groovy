package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener

class TerraformFileStandardListener extends EventListener {
  public static final DEPLOY_ONLY_ALLOWED_PATTERN = /(\.gitignore|\.pre-commit-config\.yaml|data(_[a-z0-9_-]+)?\.tf|deploy\.tf|Jenkinsfile|outputs\.tf|providers\.tf|README\.md|CHANGELOG\.md|variables\.tf|versions\.tf|files|templates|ecosystem)/
  public static final DEPLOY_MANDATORY_FILES = [
    'deploy.tf',
    '.gitignore',
    '.pre-commit-config.yaml',
    'README.md'
  ]
  public static final MODULE_MANDATORY_FILES = [
    '.gitignore',
    '.pre-commit-config.yaml',
    'main.tf',
    'variables.tf',
    'outputs.tf'
  ]

  private Script context

  TerraformFileStandardListener(Script context) {
    this.context = context
  }

  @Override
  String listenTo() {
    return TerraformEvents.PRE_PIPELINE
  }

  EventDataInterface run(EventDataInterface eventData = null) {
    if (this.isCurrentCodeTerraformDeployment()) {
      this.checkOnlyHasFiles(this.DEPLOY_ONLY_ALLOWED_PATTERN)
      this.checkContainsFiles(this.DEPLOY_MANDATORY_FILES)
    } else {
      this.checkContainsFiles(this.MODULE_MANDATORY_FILES)
    }

    return eventData
  }

  private void checkOnlyHasFiles(String validPattern) {
    for (filename in this.context.execute(script: "ls").stdout.split()) {
      if (!(filename =~ validPattern)) {
        throw new Exception("The current build is a candidate to publish but it contains a “${filename}” file. This does not comply with FX standard. For deployments, only the following file patterns are allowed: ${this.DEPLOY_ONLY_ALLOWED_PATTERN}")
      }
    }
  }

  private void checkContainsFiles(List validFiles) {
    validFiles.each { filename ->
      if (!this.context.fileExists(filename)) {
        throw new Exception("This build does not meet FX standards: a Terraform module MUST contain a “${filename}” file. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#modules.")
      }
    }
  }

  private Boolean isCurrentCodeTerraformDeployment() {
    return this.context.fileExists('deploy.tf')
  }
}
