import com.fxinnovation.nexus.PowershellApplicationRepository
import com.fxinnovation.nexus.PowershellModuleRepository
import com.fxinnovation.data.ScmInfo

def call(Map config = [:], Map closures =[:]){
    fxJob([
        pipeline: { ScmInfo scmInfo ->

            printDebug('----- fxPowershellBuild -----')

            mapAttributeCheck(config, 'powershellDockerImage', CharSequence, "fxinnovation/powershell-build:latest",  '') //Fix version when tests are done
            mapAttributeCheck(config, 'nuGetApiKey', CharSequence, 'FXPowershellModulePublisherNugetApiKey')

            def mavenrepository
            def publishModuleRepository
            if (  'master' == scmInfo.getBranch() && '' != scmInfo.getTag() ){
                config['isRelease'] = true
                config['version'] = scmInfo.getTag()

                publishModuleRepository = PowershellModuleRepository.getReleaseRepository(this,config)
                mavenrepository = PowershellApplicationRepository.getReleaseRepository(this, config)
            }
            else{
                config['isRelease'] = false
                config['version'] = "latest-${scmInfo.getBranch().replace("\\", "-").replace("/", "-").replace(" ", "_").toLowerCase()}"

                publishModuleRepository = PowershellModuleRepository.getUnstableRepository(this,config)
                mavenrepository = PowershellApplicationRepository.getUnstableRepository(this, config)
            }

            def readModuleRepository = PowershellModuleRepository.getReadRepository(this,config)

            currentBuild.displayName = "#${BUILD_NUMBER} - ${config.version}"

            config['artefactFolder'] = "_artefacts"

            printDebug('----- Configs parsed -----')

            if (closures.containsKey('preBuild') && closures.preBuild instanceof Closure){
                stage('preBuild'){
                    closures.preBuild()
                }
            }

            stage('build'){
                printDebug('----- Building -----')

                powershellCommand = dockerRunCommand(
                    dockerImage: config.powershellDockerImage,
                    environmentVariables:  [
                                        FXNexusUrl: "${readModuleRepository.getBaseUrl()}/"
                                    ],
                    additionalMounts: [:],
                    fallbackCommand:  'pwsh',
                )

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
                withCredentials([string(credentialsId: config.nuGetApiKey, variable: 'mysecret')]){
                    powershellCommand = dockerRunCommand(
                        dockerImage: config.powershellDockerImage,
                        environmentVariables:  [
                            PublishModuleUri: "${publishModuleRepository.getBaseUrl()}/",
                            NuGetApiKey: "${mysecret}"
                        ],
                        additionalMounts: [:],
                        fallbackCommand:  'pwsh',
                    )

                    execute(
                        script: "${powershellCommand} publish"
                    )
                }

                def prop = readJSON file: 'PowershellDefinition.json'
                if(prop.Type == 'PowershellApp'){
                    printDebug('----- Publishing Maven-----')
                    mapAttributeCheck(config, 'applicationName', CharSequence, prop.Name)

                    def item = mavenrepository.newItem(this, config)

                    def zips = findFiles(glob: '_artefacts/*.zip')
                    if(zips.length != 0){
                        item.publish(this,zips.path)
                        currentBuild.description = "Artefact is available at \n${item.getUrl().toLowerCase()}"
                    }
                }
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