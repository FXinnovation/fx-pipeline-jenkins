import com.fxinnovation.nexus.PowershellApplicationRepository
import com.fxinnovation.nexus.PowershellModuleRepository
import com.fxinnovation.data.ScmInfo

def call(Map config = [:], Map closures =[:]){
    fxJob([
        pipeline: { ScmInfo scmInfo ->

            printDebug('----- fxPowershellBuild -----')

            mapAttributeCheck(config, 'powershellDockerImage', CharSequence, "fxinnovation/powershell-build:latest",  '') //Fix version when tests are done

            mapAttributeCheck(config, 'nexusNugetAPIKey', CharSequence, 'FXPowershellModulePublisherNugetApiKey')
            mapAttributeCheck(config, 'nexusAPIToken', CharSequence, 'FXNexusAPIToken')

            mapAttributeCheck(config, 'nexusBaseUrl', CharSequence, 'https://artefacts.ops0.fxinnovation.com/')
            mapAttributeCheck(config, 'nexusPowershellModuleRepositoryRead', CharSequence, 'nuget-fxinnovation-powershell-module')

            def ending
            if (  'master' == scmInfo.getBranch() && '' != scmInfo.getTag() ){
                config['isRelease'] = true
                config['version'] = scmInfo.getTag()

                ending = "-releases"
            }
            else{
                config['isRelease'] = false
                config['version'] = "latest-${scmInfo.getBranch().replace("\\", "-").replace("/", "-").replace(" ", "_").toLowerCase()}"

                ending = "-featurebranches"
            }

            mapAttributeCheck(config, 'nexusRawRepositoryPublish', CharSequence, "raw-fxinnovation-powershell-application${ending}")
            mapAttributeCheck(config, 'nexusPowershellModuleRepositoryPublish', CharSequence, "nuget-fxinnovation-powershell-module${ending}")

            currentBuild.displayName = "#${BUILD_NUMBER} - ${config.version}"

            printDebug('----- Configs parsed -----')

            if (closures.containsKey('preBuild') && closures.preBuild instanceof Closure){
                stage('preBuild'){
                    closures.preBuild()
                }
            }

            stage('build'){
                printDebug('----- Building -----')

                withCredentials([usernamePassword(credentialsId: config.nexusAPIToken, usernameVariable: 'nexusAPITokenUsername', passwordVariable: 'nexusAPITokenPassword')]){
                    powershellCommand = dockerRunCommand(
                        dockerImage: config.powershellDockerImage,
                        environmentVariables:  [
                                            FXNexusBaseUrl: "${config.nexusBaseUrl}",
                                            FXNexusAPITokenUsername: "${nexusAPITokenUsername}",
                                            FXNexusAPITokenPassword: "${nexusAPITokenPassword}",
                                            FXNexusPowershellModuleRepositoryRead: "${config.nexusPowershellModuleRepositoryRead}"
                                        ],
                        additionalMounts: [:],
                        fallbackCommand:  'pwsh',
                    )
                }

                execute(
                    script: "${powershellCommand} build '${config.version}'"
                )

                printDebug('----- Building Done-----')
            }
            if (closures.containsKey('postBuild') && closures.postBuild instanceof Closure){
                stage('postBuild'){
                    closures.postBuild()
                }
            }

            if (closures.containsKey('prePublish') && closures.prePublish instanceof Closure){
                stage('prePublish'){
                    closures.prePublish()
                }
            }

            stage('publish'){
                printDebug('----- Publishing from image-----')
                withCredentials([string(credentialsId: config.nexusNugetAPIKey, variable: 'nexusNugetAPIKeySecret')]){
                    withCredentials([usernamePassword(credentialsId: config.nexusAPIToken, usernameVariable: 'nexusAPITokenUsername', passwordVariable: 'nexusAPITokenPassword')]){
                        powershellCommand = dockerRunCommand(
                            dockerImage: config.powershellDockerImage,
                            environmentVariables:  [
                                FXNexusBaseUrl: "${config.nexusBaseUrl}",
                                FXNexusAPITokenUsername: "${nexusAPITokenUsername}",
                                FXNexusAPITokenPassword: "${nexusAPITokenPassword}",
                                FXNexusPowershellModuleRepositoryRead: "${config.nexusPowershellModuleRepositoryRead}",
                                FXNexusRawRepositoryPublish: "${config.nexusRawRepositoryPublish}",
                                FXNexusPowershellModuleRepositoryPublish: "${config.nexusPowershellModuleRepositoryPublish}",
                                FXNexusNugetAPIKey: "${nexusNugetAPIKeySecret}"
                            ],
                            additionalMounts: [:],
                            fallbackCommand:  'pwsh',
                        )

                        execute(
                            script: "${powershellCommand} publish"
                        )
                    }
                }

                currentBuild.description = readFile file: '_artefacts/jobDescription.txt'

                printDebug('----- Publishing Done -----')
            }

            if (closures.containsKey('postPublish') && closures.postPublish instanceof Closure){
                stage('postPublish'){
                    closures.postPublish()
                }
            }
        }
    ])
}