# Changelog

## 0.25.3

feat: Update the credentials for Azureterraform test pipeline.

   * fxAzureTerraformPipeline:
      * feat: Update the test credentials `fxazure-terraformtests-service-principal` to `fxazure-terraformtests-12K-service-principal` which will use fx 12K subscription.
      
## 0.25.2

fix(terraform): Prevent repo names ending with - and allow first element of the name to be 2 digits min, not 3

## 0.25.1

* fix: Add `resgisterservice` to `fxCheckout` function
* fix: Bug in `pipelineCookbook` function
 
## 0.25.0

* fix: Review pod architecture to start a new container
* fix: Do not disply docker network when bridge
* feat: Add a waiting kind to be ready
* tech: pin fxinnovation/kind docker image to 0.2.0
* feat: Add an initPod to increase inotify max user watches to 524288 (see https://github.com/kubernetes/test-infra/pull/13515)

## 0.24.0

* feat: Add a Dependency Injection component (`IOC` class)
* feat: Add gradle configuration file for library loading
* feat: Add first implementation of unit testing

## 0.23.1

* hotfix: Wrong default join on groovy Map 

## 0.23.0

feat: Add possibility to run kind (Kubernetes IN Docker) on terraform pipeline. This allow use to test kubernetes generic module directly on a local test cluster.

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
