# Changelog

## 0.23.0

feat: Add possibility to run kind (Kubernates IN Docker) on terraform pipeline. This allow use to test kubernates generic module directly on a local test cluster.

* fxJob: 
  * feat: Add `podVolumes` option

* fxTerraform:
  * feat: Add `runKind` option.

* terraform:
  * feat: Add `dockerNetwork` option
  * fix: missing global configuration check on `plan` and `apply` functions

* dockerRunCommand:
  * feat: Add `network` option

## 0.22.1

* pipelinePlaybook: fix call to addOnlyIfNotExist -> addClosureOnlyIfNotDefined

## 0.22.0

* feat: Add `DeprecatedMessage` and `DeprecatedFunction` classes to manage library deprecation
* feat: Add `fxAzureTerraformPipeline` for specific tests on FX azure account
* feat: Add `standardAzureTerraformPipeline` for generic use
* Breaking : `fxAzureTerraform` is now deprecated and will be deleted in the future

## 0.21.0

* feat: Add classes to set Observer pattern (to replace closure system in the future)

## 0.20.1

* Fix: Wrong `pipelineTerraform.fmt` signature when called in `fxTerraform` method.  

## 0.20.0

* Tech: refactor pipeline to use closureHelper when it's possible (issue #36)

* ClosureHelper:
  * Feat: Add method addClosure and addClosureOnlyIfNotDefined

* fxJob: 
  * Add possibility to change pod slave docker image (attribute `podImageName`) and version (attribute `podImageVersion`)

## 0.16.0

* fxJob:
  * Feat: Add pod options: podCloud, podName, podNamespace and podNodeUsageMode
  * Breaking: Rename \*notify closures to \*notification
  * Breaking: Notification closure must now accept a String input argument
  * Feat: Add two new closures, prePipeline and postPipeline
  * Feat: Add new headerMessage variable to overwrite “TEDI announcement”

* fxTerraformWithUsernamePassword:
  * Feat: Add possibility to add closures

* fxTerraform:
  * Feat: Add possibility to add closures (except postNotificatio and pipeline)

* Release:
 * fxInspecDockerImage
