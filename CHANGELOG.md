# Changelog

## 2.2.

* feat: (standardJob) rename former function fxJob() into standardJob()

## 2.2.1

* fix: (dockerLoginListener) wrong method call for dockerLoginListener
* feat: (dockerLoginListener) add more debug output when not call

## 2.2.0

* fix: `commandTarget` must need to be set inside a `-chir` flag since TF 0.14.x and later

## 2.1.2

* fix: (terraform) add color for terraform actions

## 2.1.1

* fix: (terraform) add a lot of debug messages
* fix: (terraform) allows data files in deployments
* fix: (terraform) pass commonOption to init as well

## 2.1.0

* feat: support non master branch as default branch

## 2.0.3

* fix: (terraform) allows files and templates folders in deployments

## 2.0.1

* fix: (terraform) allows Jenkinsfile in deployments
* fix: (terraform) allows versions.tf in deployments
* fix: (DockerRunnerHelper) makes sure DockerRunnerHelper.run returns its execute results

## 2.0.0

* feat: (DI component) Only add a listener to the listeners bag when it’s not already inside
* feat: (DI component) shows debug information when env.DEBUG=true when calling a listener
* feat: (terraform) adds listeners for init, fmt, validate, plan, apply & destroy rather than using closure
* feat: (terraform) adds FX specific listeners for init, repo naming standard, file naming standard
* refactor: (terraform) changes fxTerraform to standardTerraform, first being FX specific, the other generic
* feat: moves fxCheckout() to a listener of INIT event to run it as soon as possible
* feat: (fxCheckout) makes better effort to try to find default branch
* feat: moves fxCheckoutTag() in the same listener than fxCheckout
* feat: (fxJob) moves docker login in its own listener
* feat: (fxJob) moves header display in its own listener
* feat: (fxJob) moves pre-commit in its own listener
* feat: (fxDockerImage) move from private pipeline to public
* feat: (fxDockerImage) create standardDockerImage for standardPipeline
* refactor: (runDockerCommand) Adds class to run docker command and deprecates dockerRunCommand
* refactor: (inspec) Rewrite function: allow --reporter option, use OptionStringFactory and DockerRunnerHelper

## 1.4.2

* fix: (scmInfo) makes sure the correct repository name is returned

## 1.4.1

* fix: (dockerRunCommand) wrong key value delimiter for environment

## 1.4.0

* feat: (dockerRunCommand) build arguments with optionStringFactory instead of manual
* fix: fixes error on all build due to the access to environment variable

## 1.3.0

* feat: (fxJob) allows to run fxJob locally
* feat: (fxJob) auto-set launchLocally to true if env var JENKINS_LOCAL exists
* feat: (fxJob) auto-set dockerDataBasepath to true if env var JENKINS_DOCKER_DATA_BASEPATH exists
* feat: (fxJob) disables cleanWs when run locally
* feat: (dockerRunCommand) allows to change docker run data basepath
* feat: (dockerRunCommand) allows to run docker commands as deamon
* feat: (dockerRunCommand) allows to add name and entrypoint params
* feat: (fxCheckout) fall back to `git branch` if BRANCH_NAME env var is not set

## 1.2.3

* fix: fxJob properties (permit override)
* chore: bump pre-commit hooks

## 1.2.2

* fix: regex for terraform deployment filenames

## 1.2.1

* fix: Resolves file name checking on terraform deployments

## 1.2.0

* feat: add cache volume on slaves

## 1.1.0

* feat: Add pod annotation on slaves

## 1.0.2

* fix: Some other fixes

## 1.0.1

* fix: Typo's to be fixed

## 1.0.0

* feat: (BREAKING) Add dockerhub login in fxJob()

## 0.30.0

* chore: Run pre-commit on the whole repository
* chore: Update pre-commit config
* feat: Allow terraform deployment to have multiple data files

## 0.29.1

* fix: Regex for TF deployment repo name

## 0.29.0

* feat: Allow for non empty plan on outputs

## 0.28.0

* feat: Add standardAwsNukeWithCredentials
* feat: Add standardAwsNukeWithAssumeRole

## 0.27.0

* feat: Add `data.tf` to autorized files on fxTerraform deployments
* feat: Add `versions.tf` to autorized files on fxTerraform deployments

## 0.26.0

* feat: Add `awsNuke` function
* feat: Add `pipelineAwsNuke` function

NOTE: To use `awsNuke` function, you need to install [MaskPassword pluggin](https://plugins.jenkins.io/mask-passwords)

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
