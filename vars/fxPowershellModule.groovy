/****************************************
// This file should not be used anymore
// Obsolete
*****************************************/

import com.fxinnovation.nexus.PowershellModuleRepository
import com.fxinnovation.data.ScmInfo

def call(Map config = [:], Map closures =[:]){
  fxJob([
    pipeline: { ScmInfo scmInfo ->

      printDebug('----- fxPowershellModule -----')

      if (  'master' == scmInfo.getBranch() && '' != scmInfo.getTag() ){
        config['version'] = scmInfo.getTag()
        config['isRelease'] = true
        config['publishRepository'] = PowershellModuleRepository.getReleaseRepository(this,config)
      }
      else{
        config['version'] = "${scmInfo.getBranch().replace("\\", "").replace("/", "").replace(" ", "").replace("-", "").toLowerCase()}"
        config['isRelease'] = false
        config['publishRepository'] = PowershellModuleRepository.getUnstableRepository(this,config)
      }

      currentBuild.displayName = "#${BUILD_NUMBER} - ${config.version}"

      config['artefactFolder'] = "_artefacts"

      mapAttributeCheck(config, 'nuGetApiKey', CharSequence, 'FXPowershellModulePublisherNugetApiKey')

      printDebug('----- Configs parsed -----')

      if (closures.containsKey('preBuild') && closures.preBuild instanceof Closure){
        stage('preBuild'){
          closures.preBuild()
        }
      }
      stage('build'){

        printDebug('----- Building -----')
        powershellModule.buildModule(config)
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
        printDebug('----- Publishing -----')
        powershellModule.publishModule(config)
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