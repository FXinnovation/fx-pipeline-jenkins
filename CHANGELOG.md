# Changelog

# 0.16.0

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
