# Changelog

## 0.18.0

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
