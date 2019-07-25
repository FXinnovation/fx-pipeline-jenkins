# Description

Build powershell application and publish it as a zip on nexus

# Fonctionnality

- Add powershell module dependencies
- Run all unit tests then publish the results to jenkins
- Can merge mutliple application togetter
- Publish a zip file to nexus
        ex : https://artifacts.dazzlingwrench.fxinnovation.com/repository/powershell-application-featurebranches/com/fxinnovation/myapplication/latest-feature-myfeaturebranch/myapplication-latest-feature-myfeaturebranch.zip
        ex : https://artifacts.dazzlingwrench.fxinnovation.com/repository/powershell-application/com/fxinnovation/myapplication/1.0.0/myapplication-1.0.0.zip

# Parameters

## applicationName (Mandatory)

Application's name

## nexusUrl

Url to the nexus repository

    Default : 'https://artifacts.dazzlingwrench.fxinnovation.com'

## powershellApplicationReleaseRepository

Will deploy to this repository if on master and the commit is tag with a version.

    Default : 'powershell-application'
    Note : Will skip the publish if the file have already been published

## powershellApplicationUnstableRepository

Will deploy to this repository if not on master or the commit have not been tagged with a version.

    Default : 'powershell-application-featurebranches'
    Note : Will always publish and override if already published

## client

This field is used as a groupid in nexus. 

    Default : 'com.fxinnovation'

## powershellModuleRepository

Will register this powershell repository as FXNexus

    Default : 'https://artifacts.dazzlingwrench.fxinnovation.com/repository/powershell-module/'

## powershellDockerImage

Docker image used to build the application.
It contains all requirements :
    - Powershell 6.+
    - .Net core 2.+
    - FX Powershell build modules

    Default : 'fxinnovation/powershell-build'

## dependencies

This parameter is used to merge applications togetter.

The content of src will become the content of all applications + the current items of src. The order matter.

    parameters : 
        - nexusUrl
        - powershellApplicationReleaseRepository
        - powershellApplicationUnstableRepository
        - client
        - applicationName
        - isRelease (Default : true)

### isRelease

# Jenkins file

## Minimal requirements 

```groovy
fxPowershellApplication(
   applicationName: 'myapplication'
 )
 ```

 ### Complete list

 ```groovy
fxPowershellApplication(
   applicationName: 'myapplication',
   client: 'com.fxinnovation',
   nexusUrl: 'https://artifacts.dazzlingwrench.fxinnovation.com',
   powershellApplicationReleaseRepository: 'powershell-application',
   powershellApplicationUnstableRepository: 'powershell-application-featurebranches',
   powershellDockerImage: 'fxinnovation/powershell-build',
   powershellModuleRepository: 'https://artifacts.dazzlingwrench.fxinnovation.com/repository/powershell-module/',
   dependencies: [
      [
        applicationName: 'app1',
        version: 'latest-feature-myfeaturebranch',
        isRelease: false
      ],
      [
        applicationName: 'app2',
        version: '1.0.0'
      ]
   ]
 )
 ```