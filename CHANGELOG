0.15.0 / 2020-01-17
==================

  * feat: adds fxAzureTerraform for a standard pipeline for Azure in FX
  
0.14.2 / 2020-01-16
==================

  * fix: (dockerImage) makes sure no empty tag is used to build or publish

0.14.1 / 2020-01-16
==================

  * fix: (dockerImage) makes sure push is done one by one
  * refactor: (dockerImage) reorganizes mapAttributeCheck for deduplication
  * feat: (docker) adds check on config.registry when credentialsId are given

0.14.0 / 2020-01-15
==================

  * feat: allows publishing as rc
  * fix: fixes ScmInfo condition
  * fix: fixes isCurrentTagLatest
  * fix: ScmInfo Boolean functions returns booleans
  * fix: explictly calls toString on printDebug
  * feat: hides command outputs in fxCheckout as it can be seen with debug
  * feat: adds toString() to OptionStringFactory
  * refactor: improves execute and dockerRunImage
  * refactor: simplifies dockerRunCommand pipeline
  * fix: passes the context to OptionStringFactory
  * fix: passes scmInfo at getAllTags()
  * fix: changes scope of optionStringFactory
  * fix: makes sure docker build also have tags
  * feat: create ClosureHelper to deduplicate closures calls
  * feat: adds getBranchAsDockerTag for ScmInfo
  * refactor: gives more information about ScmInfo for debug
  * feat: for tag to be latest, it must not have pre-release
  * refactor: deduplicates more code in dockerImage
  * refactor: deduplicates code in dockerImage.build
  * doc: adds basic dockerImage documentation

0.13.2 / 2020-01-09
===================

  * fix/ wrong condition
  * fix: makes isPublishableAsDev work too
  * fix: getPreReleaseTag returns always string too
  * hotfix: makes sure String function always returns a string
  * fix/ prerelease tag

0.13.1 / 2020-01-09
===================

  * fix: returns master as default branch

0.13.0 / 2020-01-08
===================

  * feat: adds isPublishableAsAnything
  * refactor: removes isPublishableAsLatest
  * doc: updates error message
  * fix: fixes class instanciation in fxCheckout
  * fix: fixes import in fxCheckout
  * feat: allows debug printing of ScmInfo
  * refactor: removes docker enhancement for anohter PR
  * feat: allows providers.tf files for Terraform deployments
  * refactor: updates existing pipelines to work with ScmInfo object
  * doc: updates fxCheckout documentation
  * feat: makes fxCheckout populate the ScmInfo object
  * feat: adds SCmInfo data object
  * refactor: removes GITBranch and pipeline helper
  * feat: only publish if the tag is master or does not exists

0.12.1-dev / 2019-12-22
=======================

  * this is temporary

0.12.0-dev / 2019-12-22
=======================

  * tech/ add debug
  * tech/ changelog
  * feature/ add pod options
  * fix: comment out show command for terraform
  * tech/ remove old code
  * fix/ rename optionsString to optionStringFactory

0.11.0 / 2019-11-08
===================

  * refactor: uses delimiter getter instead of direct access
  * fix: fixes arguments for OptionString
  * fix: fixes wrong argument
  * test: debug
  * refactor: debug
  * fix: fixes arguments for OptionString
  * fix: fixes import for Terraform
  * refactor: moves classes around
  * Testing stuff out
  * Fix
  * Making some other changes
  * Add mount option for terraform
  * comment fx_notify out
  * Remove fxNotify
  * tech/ add --color=always args to pre-commit docker
  * fix/ color
  * feat/ add header for PRs
  * refactor: passes pre-commit docker image as a parameter
  * feat: adds pre-commit command
  * fix: sets back the initial condition
  * test: fix
  * fix: adds dot in the filename for pre-commit
  * feat: runs pre-commit at the beginning of fx pipeline
  * Revert "Added feature"
  * Revert "Add feautre2"
  * Add feautre2
  * Added feature

0.10.2 / 2019-09-05
===================

  * fix: passes commonOptions to fmlt
  * fix: pass config
  * feat: removes all vars for fmt
  * fix: debug
  * fix: fixes parameters for fmt
  * fix: workaround groovy global variable mess
  * refactor: print debugs of errors

0.10.1 / 2019-09-04
===================

  * fix: fixes pipeline
  * fix: fixes condition

0.10.0 / 2019-09-04
===================

  * refactor: makes private methods privates
  * feat: calls fmt before loops for Terraform
  * feat: splits terraform fmt & terraform validate in main pipeline
  * Added notification mechanism
  * Update helm.groovy
  * Add option for inspec to exit 0 even when tests are skipped
  * Terraform: Add new providers
  * Wirte different test result file for each test.
  * Add better error message
  * Set build period to weekly, less load on jenkins less costs
  * Fix timeout issues
  * Typo
  * Add pre-commit config and add slave sizes
  * Add documentation
  * Add default timeout of 10 hours on any job.
  * Fix
  * Add fmtOptions
  * Merged in hotfix/notification (pull request #114)
  * Merged in feature/fxTerraformWithSecretFile (pull request #115)
  * Add commonOptions to something
  * Set docker image back to fxinnovation one
  * FIX: commonOptions
  * Add possibility to pass common options to terraform helper
  * Accept inspec license
  * Fix inspec used by docekr
  * Changing inspec docker image by actual inspec image
  * Fixed typo
  * Fixed inspec pipeline
  * commas
  * Typo in switch statement
  * Set docker image to 0.12.3
  * Making this work
  * Fixing problems
  * Adapting to make it work
  * Add debug
  * Made credential dynamic
  * Add pipeline for auth with secret file
  * feat: prepares terraform show to be used in tasks
  * Merged in feature/prepareTerraformShow (pull request #113)
  * Fixing some small stuff
  * Add no failures when failing to notify
  * feat: prepares terraform show to be used in tasks
  * Add inspec to workflow, should not break anything else
  * Add azuread as a valid provider for naming patterns
  * Merged in hotfix/terraformDeployNames (pull request #111)
  * doc: changes error message
  * fix: allows hyphens in deployments names
  * Merged in kube_secrets (pull request #110)
  * Remove update because the command doesn't exist
  * Add task for hadnling kubernetes secrets
  * Merged in hotfix/terraformTargets (pull request #109)
  * fix: targets => target
  * Merged in hotfix/parameters_aws (pull request #108)
  * remove escape
  * remove single quotes
  * fix it 2
  * refactor code
  * fix it
  * add env variable
  * add sh
  * try
  * fix Secret to String
  * try new class
  * Add backslashes
  * add method to fxString
  * Merged in hotfix/FXString (pull request #107)
  * fix: overrides needed method for CharSequence
  * Merged in feature/terraformStateMv (pull request #106)
  * fix: changes some java doc and makes delegation returns
  * feat: improves FXString by making it implements the same as java.lang.String
  * feat: adds FxString decorator
  * feat: adds Terraform state mv
  * Merged in feature/awsssm (pull request #105)
  * forget use toString
  * fix typo
  * refactor + add overwrite option
  * Add awsSsm helper
  * Merged in refactor/execute (pull request #103)
  * Fixed some other stuff
  * Merged in hotfix/pipelinedocker (pull request #104)
  * Fix dockerImage
  * Display error message for terraform fmt command
  * Making this better
  * Debug
  * Adding some more refactoring
  * Refactoring execute
  * Merged in feature/lessplusx (pull request #101)
  * Merged in feature/inputAsker (pull request #100)
  * Merged in hotfix/dockerRepositories (pull request #102)
  * Fix multiples repositories
  * This should allow for less +x appearing in job output
  * "fix: debug"
  * Merged in feautre/pipelineHelm (pull request #99)
  * Removed commented code
  * Fixing cosmetics
  * Variablize stuff
  * Added deploy pipeline
  * Added fx pipeline for publishing helm charts
  * Fix
  * Testing
  * Fixed
  * Fix
  * Fix
  * Testing
  * Make small fixes
  * Added pipeline
  * feat: adds UserInput & OptionInputAsker
  * Merged in feature/giteaAPIHelper (pull request #85)
  * Merged in hotfix/removeVars (pull request #98)
  * remove vars in terraform show
  * Merged in hotfix/pipeline (pull request #97)
  * fix: syntax
  * Merged in hotfix/variousFixesTerraform (pull request #95)
  * add terraform show
  * Merged in fix/terraformOutput (pull request #94)
  * add vars for refresh
  * try to fix terraform output
  * Merged in fix/chefdk (pull request #93)
  * pin to version 3.3.0
  * test: debugs
  * refactor: organizationId => organizationName
  * fix: fixes variable
  * fix: Interger => Integer
  * feat: allows a Integer instead of CharSequence for Id
  * Merged in hotfix/failedRevert (pull request #92)
  * init
  * Merged in hotfix/revert (pull request #91)
  * revert changes
  * Merged in feature/addOutputForTerraform (pull request #90)
  * fix : remove json output
  * fix subCommand
  * add output
  * add output
  * Merged in hotfix/allowOutputs (pull request #89)
  * fix: allows outputs.tf file for fxTerraform pipeline
  * Merged in hotfix/addImportOptionString (pull request #88)
  * add import
  * Merged in feature/databagFromFile (pull request #87)
  * fix PR comments
  * fix secretId
  * fix secretId
  * dbug 3
  * try
  * dbug
  * invert condition
  * refactor: adds missing s
  * fix null pointer exception
  * fix null pointer exception
  * add bag creation
  * fix targetCommand
  * fix empty bag list
  * databag to data bag
  * fix wrong arg
  * fix knife helper
  * remove dbug
  * dbug 2
  * dbug
  * p# Please enter the commit message for your changes. Lines starting
  * add databagList
  * init
  * feat: adds getOrga and getRepos helpers
  * Merged in feature/pipelineTask (pull request #84)
  * refactor: renames selectProject to selectTarget
  * feat: adds fxTask generic pipeline
  * feat: adds generic pipeline for tasks
  * Merged in hotfix/fixPowershellHelp (pull request #83)
  * change default mount and env to map
  * Merged in feature/inspecDockerParameters (pull request #81)
  * Merged in hotfix/dockerImage (pull request #82)
  * Made namespace optionnal for publish
  * Add ok parameters
  * Fix version command
  * Fix version command
  * feat: uses OptionString object
  * feat: allows to pass extra mounts and env var to docker inpacker
  * Merged in refactor/Terraform (pull request #80)
  * fix: changes lock to reconfigure
  * test: debug
  * refactor: moves OptionString to its own namespace
  * Merged in featur/no_namespace (pull request #79)
  * test: debug
  * Fix
  * Typo
  * Yoda style
  * Made namespace optionnal because ECR
  * feat: adds OptionString constructor
  * feat: adds OptionString constructor
  * fix: changes updateContent signature
  * fix: changes condition again
  * Merged in hotfix/exemple (pull request #78)
  * fix: fixes examples conditions making pipeline deployement fail
  * fix: fixes OptionString ArrayList loop
  * fix: changes unexisting variable
  * fix: changes OptionString.add signature
  * feat: creates an OptionString object to remove duplication
  * Merged in feautre/improveDockerImage (pull request #76)
  * Added aws helpers (minimal)
  * Made credentialId parameter optionnal
  * Merged in var/refactor (pull request #73)
  * Merged in feature/pipelinePlaybook (pull request #75)
  * typo
  * typo
  * add hooks
  * add converge closures
  * add converge closures
  * add galaxy
  * fix
  * Merged in feature/ansibleGalaxy (pull request #74)
  * = is enough
  * Approve me, you need ;)
  * Import
  * refactor the cookstyle
  * Merged in feature/addFXAnsiblePipeline (pull request #72)
  * feat: refactors and completes playbook pipeline
  * Merged in feature/addAnsiblePlaybookHelper (pull request #67)
  * doc: removes arguments that does not exists in documentation
  * fix: uses correct ansible docker image
  * fix: changes variables name
  * feat: adds ansible-playbook helper
  * Merged in feature/autoFindExamples (pull request #71)
  * refactor: removes print
  * refactor: removes prints
  * test: debug
  * fix: reverts additionalMount docker to ArrayList
  * fix: transforms additionalMounts to a Map
  * fix: fixes indentation
  * fix: adds deprecated warning
  * fix: makes sure it works with empty command
  * feat: auto discover examples for Terraform pipeline
  * Merged in hotfix/dockerEnvironmentVariables2 (pull request #70)
  * Fix typo
  * Merged in hotfix/dockerRunCommand (pull request #69)
  * Fix type list to map
  * Merged in feature/adaptAnsibleLint (pull request #66)
  * Merged in hotfix/dockerEnvironmentVariables (pull request #68)
  * Fix typo in test for dockerEnvironmentVariables
  * refactor: changes ansiblelint function to use latest docker
  * refactor: changes dockerRunCommand to use mapAttributes
  * Merged in bugfix/execute (pull request #65)
  * Merged in feature/foolProofValidation (pull request #64)
  * Added example to make it more explicit
  * Making instructions more clear
  * making variables local to the function.
  * Merged in hotfix/moreEntropy (pull request #63)
  * Add foolProofValidation
  * <execute> Changed filePrefix to a UUID
  * Merged in feature/yarn (pull request #59)
  * Merged in feature/dockerImageImprove (pull request #60)
  * Merged in feature/python (pull request #62)
  * adding fxPython txt message
  * adding fxPython helper.
  * Merged in feature/python (pull request #61)
  * making lint a "make lint" requirement rather than dealing with it here.
  * making sure fallback command fails.
  * clarifying the variables for folders to be linted.
  * taking a first jab at this.
  * FIX: Usage of mapAttirbuteCheck
  * [BREAKING] Add multi registry part 2
  * [REFACTOR] Use mapAttributeCheck
  * [BREAKING] Add multiple registries
  * Minor fixes
  * Fixed typo
  * Added linting tests
  * Added test results
  * Calling the function this time
  * Fixed typo
  * Improving yarn helper
  * Fixing pipeline
  * Added fx pipeline
  * Added pipeline for yarn
  * Added yarn helper
  * Merged in hotfix/fixPlan (pull request #58)
  * fix: fixes terraform plan file name
  * Merged in hotfix/deployment (pull request #57)
  * fix: fixes the isManuallyTriggered
  * debug
  * debug
  * debug
  * debug
  * feat: adds more debug output
  * debug
  * test: debugs
  * Merged in feature/additionnalChecksTerraform (pull request #56)
  * Added json2hcl helper
  * fix: changes repo name test conditions
  * feat: adds repository name check for deployment
  * feat: adds repository name check
  * feat: adds some chekcs
  * Merged in hotfix/removeStateAfterEachTest (pull request #55)
  * feat: also makes sure that teest.out is removes
  * Merged in feature/fasterTerraform (pull request #54)
  * fix: removes state file after each tests
  * Removing refresh option
  * Trying to make terraform faster
  * Merged in hotfix/noMandatoryPublish (pull request #53)
  * fix: puts back functions inside closures
  * fix: removes the applyOptions that makes no sense in fxTerraform
  * fix: adds mandatory arguments for init and publish functions
  * feat: removes test on publish creds in fxTerraform that makes no sense
  * refactor: reorganizes code to remove unuseful functions-inside-closure
  * feat: sets default publish creds to the same as test creds
  * refactor: defines only disableConcurrentBuild
  * feat: moves functions out of the main and validate publish crreds only on publish function
  * feat: removes mandatory publish credentials
  * feat: allows merge of propertie in fxJob instead of default VS nothing
  * Merged in feature/changeStagesTerraform (pull request #52)
  * fix: adds missing quote
  * fix: reverts test on closures because it wont work
  * fix: fixes one argument of the mapAttributeCheck
  * fix: fixes the publish boolean issue
  * tech: moves private FX Terraform pipelines to public
  * refactor: changes stages and checks attributes
  * Merged in feature/jobInfo (pull request #51)
  * Removed debug
  * Add a jobInfo variables that has helper
  * Merged in feature/terraformDeploy (pull request #48)
  * refactor: changes map key/value checks locally after @julien’s comments
  * Merged in feature/fxCheckoutTag (pull request #50)
  * Merge branch 'feature/fxCheckoutTag' of bitbucket.org:fxadmin/public-common-pipeline-jenkins into feature/fxCheckoutTag
  * fix typo
  * fix paramaters check + add txt
  * init
  * fix typo
  * fix paramaters check + add txt
  * Merged in feature/typeTestFunction (pull request #49)
  * init
  * doc: modifies some errors after reviews
  * doc: modifies wrong documentation
  * refactor: changes “no key to check” error message
  * feat: adds mapAttributeCheck function
  * feat: removes test when it is a deployment
  * Merged in feature/lastTag (pull request #47)
  * Added isLastTag to fxCheckout
  * Merged in feature/printDebug (pull request #46)
  * Merged in feature/pipelinePlaybook-txtexport (pull request #44)
  * Change doc : string test -> CharSequence test
  * Merged in hotfix/terraform-backend-configs (pull request #45)
  * Change string test -> CharSequence test
  * feat: adds printDebug function
  * fix: adds missing 's' for backendConfig option
  * Addeing documentation and doing proper review
  * Doing stuff
  * Ugly fix
  * Debug
  * Making this work
  * Keep artifact
  * permit override
  * write file
  * Merged in feature/dockerPowershell (pull request #39)
  * rename parameter
  * rename parameter
  * Merged in feature/inspec (pull request #43)
  * Fix docker options type
  * Merged in hotfix/pieplinecookboko (pull request #42)
  * Stupid stuff
  * Merged in hotfix/kitchen (pull request #41)
  * Fioxing destroy configuration option
  * Test
  * Added config option, removed reporter option
  * Trying stuff out
  * Fix reporter option
  * Added reporter option
  * Making some fixes
  * Add exec command
  * Merged in hotfix/helm (pull request #40)
  * Doing stupid stuff
  * Testing with an update
  * fix variable verif 2
  * fix arg verif
  * typo
  * Another fat finger
  * Fxied small typo
  * Fixed typo
  * fix some bugs
  * add new branch
  * Merged in feautre/helm (pull request #34)
  * Merged in hotfix/kitchen (pull request #38)
  * Defined default value for docker image
  * Merged in feature/pipelinePlaybook (pull request #37)
  * Merged in feature/kitchen (pull request #33)
  * Rename ansiblelint parameter -> ansiblelintConfig
  * Fixed yoda style syntax
  * Removed unneeded comment
  * import pipelinePlaybook
  * Merged in feature/chefCookbookUpload (pull request #35)
  * fix typo
  * Merged in fix/dockerRunCommand (pull request #36)
  * Fix fallbackCommand
  * add cookbook path
  * Added pipeline for a helm deployment
  * Adding more helpers for helm
  * Added some more helpers for helm
  * Added helm helper
  * Add kitchen helper - initial version
  * Merged in ansiblelint (pull request #32)
  * Merged in feature/chefEnvironment (pull request #28)
  * Stop git diff throwing error if there was a difference ugly tough
  * Improve diff
  * Trying dit diff for colored coonfig
  * Fixing diff
  * Added prePlan and postPlan stages
  * diff doesn't work
  * Fixed typo
  * Display better diff
  * Addedi nterpolation
  * Fixing diff
  * Fixed typo
  * i'm stupid
  * Added stupid thingst
  * Fixed typo in fromat option
  * Adding some verifications
  * Added format option and added environemnt list and show
  * revert change on pipelineCookbook
  * wip 7
  * disable tests stage to test knife
  * wip 6
  * wip 5
  * wip 4
  * wip 3
  * wip 2
  * WIP
  * add return because knife always exit 0
  * Returning command
  * Fix calling knife
  * Fix calling knife command
  * add some check, change logic in knife.groovy and bug fix
  * Fixing validation type
  * Improved comment
  * Made some minor changes
  * add cookbook upload
  * wip
  * initial inspec helper
  * Merged in feature/packerPipeline (pull request #29)
  * Fixed stupid mistake
  * Merged in hotfix/dockerImage (pull request #31)
  * Fix docker push command
  * Merged in hotfix/pipelineDocker (pull request #30)
  * Adding quoptes because I'm stupid
  * Added default false to publish
  * Added packer pipeline
  * Added pipeline for chefEnvironment
  * Fixed some typos
  * add cookbookName
  * correct PR-28
  * WIP cookbook upload
  * Added envrionmentFromFile for knife.groovy
  * Added knife call command
  * Merged in feature/dockerPipeline (pull request #26)
  * Fixing stuff
  * Making it work with Guillaume
  * Fixed indent and typo in dockerImage
  * Removed debug
  * Making variables with defaults to make it easier to use
  * gjhgg
  * Removing not working code
  * Added coolness
  * Debug
  * Debug
  * No changes after this point.
  * Added empty string variable
  * Array to List
  * Fixed Array to List
  * Fixing pipelineDocker to last renaming
  * Renamed file
  * Renamed dockerPipeline
  * Added docker and dockerPipeline
  * Merged in feature/pipeline-terraform (pull request #22)
  * refactor: removes commandTargets loops
  * refactor: debug
  * Merged in fixCheckout (pull request #25)
  * Changed command for execute
  * test: without commandTargets
  * refactor: move config.publish condition to englobe pre and post actions
  * fix: fixes one default options for testPlanOptions
  * refactor: small fixes
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: debug
  * refactor: sets default options at every stage
  * refactor: reorganizes pipeline
  * Merged in feature/fixGitea (pull request #24)
  * Fixed gitea
  * Merged in feature/fixGitea (pull request #23)
  * Fixed type in gitea helper
  * feat: adds basic terraform pipeline skeleton

0.9.0 / 2019-02-07
==================

  * Merged in feature/fixPrivateCookbook (pull request #21)
  * Added options for foodcritic for a private cookbook
  * Merged in feature/publicCookbookPipeline (pull request #20)
  * Removed unnecessary code
  * Removed uneccessary test
  * Listening to guillaume
  * Merged in feature/docker-run (pull request #18)
  * Added private cookbook pipeline
  * Added genric cookbook pipeline and adapted public cookbook pipeline
  * Removed debug
  * TRying to do stuff
  * Added debug
  * Fixing some stuff making test cleaner
  * tech: debug
  * tech: debug
  * feat: allows to add additionalMount and env variableto docker command
  * Merged in feature/refactorGitea (pull request #19)
  * Making even better
  * Do some great stuff
  * refactor: uses dockerRunCommand in terraform, packer, foodcritic and cookstyle
  * refactor: adapts dockerRunCommand to handle empty fallbackCommand
  * refactor: cleans eaches in dockerRunCommand
  * refactor: some fixes
  * feat: adds docker run command helper
  * Merged in feature/terraform-execute (pull request #17)
  * Finishing touch
  * Debug
  * Trying stuff out
  * Trying stuff out
  * Trying stuff out
  * Trying more things out
  * Initial commit
  * Removed debug
  * Trying to make it work again
  * Fixed typo
  * Added debug
  * Typo: fat fingers
  * gitea - bad copy pasta, fix typo
  * fixed typo in gitea
  * Refactor gitea to avoid code duplication
  * fix: checks input of execute to be CharSequence instead of String
  * fix: pass map instead of string for executes
  * refactor: simplifies command lines
  * refactor: uses execute instead of sh
  * Merged in feature/advancedCheckout (pull request #16)
  * Added a break statement, should increase performance
  * Updated documentation
  * Removed debug
  * Removed some debug and fixed variable name
  * Added some debug
  * Fixing api error
  * Fixed typo
  * Adding debug
  * Fixed typo
  * Added some stuff
  * Trying stuff out
  * Added partial feautre
  * Renamed function and added documentation
  * Added getCurrentUser
  * Fixing logic
  * dsfasdfa
  * Trying to make this wor
  * Making this work please
  * TRying
  * Continued development...
  * Lock S-foils in attack position
  * Commit committed....
  * Some bugs fixed
  * Replace all whitespaces with tabs.
  * Toto
  * Trying stuff out
  * Fixed typo
  * TRying to make this work
  * Trying stuff out
  * Trying
  * Testing
  * TRying stuff out
  * Making basic stuff work
  * Making this work
  * Trying to fix stuff
  * Added advance checkout feature
  * Merged in feature/gitea (pull request #15)
  * Removed uncomment
  * Uncomment stuff
  * Added Documentation for gitea
  * Trying to return thins
  * Merged in hotfix/execute (pull request #14)
  * Improve script readabiltiy
  * Removing finally statement
  * Removed finally
  * DEbug
  * TRying stuff out
  * Trying this out
  * Making this better
  * Fixed typo
  * Adding a post helper
  * Trying to fetch pull requests
  * Added gitea groovy

0.8.0 / 2019-01-25
==================

  * Merged in feature/packer (pull request #13)
  * Fixed variable name
  * Feed. You. Stuff. No time.
  * fixed errors in the previous commit
  * Forgot def
  * Several fixes
  * Added packer helpers
  * Merged in feature/better_sh (pull request #12)
  * Fixed typo in docs
  * Fixed typo
  * Improved comment
  * Added comment for sleep
  * Added sleep to see if it's a race condition
  * Cosmetics
  * Fixing typo
  * Adding quiting with correct exit code
  * Added documentation
  * Remove some output
  * Adding cool stuff
  * Escaping prison cells
  * This might be simpler
  * Testing`
  * Trying something new
  * More debug
  * Adding some debug
  * Trying to fix pipestatuses
  * Fixed typo
  * Try catch everything and hope it works
  * Trying to make this work
  * Adding debug
  * Debug
  * Trying to get piepstatus working
  * Trying something new
  * Trying
  * Trying something else
  * Trying not to fail
  * Trying this another way
  * debug
  * Trying to handle all cases over and over
  * Trying without pipefail
  * Trying to handle all cases
  * Removed some comments
  * Making things less visible
  * Trying all steps again
  * Step by step
  * Doing this step by step
  * Added pipefail for better error logging
  * trying with bash
  * Trying to debug
  * Added debug
  * Fixed stupid mistake
  * Escaping prison
  * Trying something new
  * fixed typo
  * Adding execute helper

0.7.0 / 2019-01-21
==================

  * Merged in feature/terraform-fmt (pull request #11)
  * feat: returns ouptut of terraform command
  * fix: adds checks & option extends
  * doc: updates doc & small typo fixes
  * feat: adds terraform fmt helper
  * Merged in feature/rocketchat (pull request #10)
  * Added cool stuff
  * Added quiet flag
  * Trying some UTF-8
  * TRying to encode the correct part of the url
  * Improve encoder
  * Fixing things
  * More ignore
  * Added debug
  * Fixed typo
  * Trying to make this encoding working
  * Trying to do same simple encoding
  * Fixed typo
  * Trying to encode url
  * Allow for not finding users
  * Added some rocketchat helpers
  * Merged in hotfix/cookstyle (pull request #9)
  * double quotes please

0.6.0 / 2018-12-27
==================

  * Merged in feature/foodcritic (pull request #7)
  * Added foodcritic helpers

0.5.0 / 2018-12-27
==================

  * Merged in feature/stove (pull request #8)
  * Adding stove

0.4.0 / 2018-12-17
==================

  * Merged in feature/terraform_slowRefresh (pull request #6)
  * Removed file for another pull request and cleaned out slowRefresh
  * Added Destroy to terraform
  * Fixed naming issues
  * Created docker class
  * Fixed typo
  * Fixed method name
  * Added Json2hcl class
  * Making sure all is refreshed
  * Fixing small things
  * Doing 5 by 5
  * Fixed typo in target
  * Fixed typo
  * Add sleep
  * Added possibility of doing a "slow refresh" of terraform state

0.3.0 / 2018-12-13
==================

  * Merged in feature/terraform_refresh (pull request #5)
  * Add refresh method to terraform variable and documentation

0.2.2 / 2018-12-12
==================

  * Merged in fix/typo (pull request #4)
  * Fixed typo

0.2.1 / 2018-12-12
==================

  * Merged in hotfix/terraform (pull request #3)
  * Fixed typo in terraofrm.groovy

0.2.0 / 2018-12-10
==================

  * Merged in feature/cookstyle (pull request #2)
  * Added Cookstyle

0.1.0 / 2018-12-10
==================

  * Merged in feature/terraform (pull request #1)
  * Added some readme
  * Fxied documentation
  * terraform - Fixed a typo in parameter validation
  * Added documentation
  * Add terraform pipeline helpers.
  * Add LICENSE
  * Initial commit
