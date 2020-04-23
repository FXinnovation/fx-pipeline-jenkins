package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventListener
import com.fxinnovation.util.FileUtils

class TerraformFileStandardListener extends EventListener {
  public static final DEPLOY_ONLY_ALLOWED_FILES = [
    'deploy.tf',
    'variables.tf',
    'outputs.tf',
    'providers.tf',
    '.gitignore',
    '.pre-commit-config.yaml',
    'README.md'
  ]
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
    print(this.context.sh('ls'))

    if (this.isCurrentCodeTerraformDeployment()) {
      this.checkOnlyHasFiles(this.DEPLOY_ONLY_ALLOWED_FILES)
      this.checkContainsFiles(this.DEPLOY_MANDATORY_FILES)
    } else {
      this.checkContainsFiles(this.MODULE_MANDATORY_FILES)
    }

    return eventData
  }

  private void checkOnlyHasFiles(List validFiles) {
    for (filename in this.context.execute(script: "ls").stdout.split()) {
      if (!validFiles.contains(filename)) {
        throw new Exception("The current build is a candidate to publish but it contains a “${filename}” file. This does not comply with FX standard. For deployments, create a single “deploy.tf” with optional “${validFiles.join("/")}” files.")
      }
    }
  }

  private void checkContainsFiles(List validFiles) {
    this.context.println(validFiles)
    this.context.println(this.context.execute(script: "ls").stdout.split())
    this.context.println(FileUtils.exists('.gitignore'))
    this.context.println(this.context.fileExists('.gitignore'))
    validFiles.each { filename ->
      if (!FileUtils.exists(filename)) {
        throw new Exception("This build does not meet FX standards: a Terraform module MUST contain a “${filename}” file. See https://dokuportal.fxinnovation.com/dokuwiki/doku.php?id=groups:terraform#modules.")
      }
    }
  }

  private Boolean isCurrentCodeTerraformDeployment() {
    return FileUtils.exists('deploy.tf')
  }
}
