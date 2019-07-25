# Description

Build powershell module and publish it on nexus

# Fonctionnality

- Run all unit tests then publish the results to jenkins
- Publish using the built-in Publish-Module in powershell

# Parameters

## moduleName (Mandatory)

Module's name

## description

Describe what the module do and will be added to the psd1

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

# Jenkins file

## Minimal requirements 

```groovy
fxPowershellModule(
   moduleName: 'mymodule'
 )
 ```

 ### Complete list

 ```groovy
fxPowershellApplication(
   applicationName: 'mymodule',
   description: 'This is a dummy module that is completly useless',
   powershellDockerImage: 'fxinnovation/powershell-build',
   powershellModuleRepository: 'https://artifacts.dazzlingwrench.fxinnovation.com/repository/powershell-module/'
 )
 ```