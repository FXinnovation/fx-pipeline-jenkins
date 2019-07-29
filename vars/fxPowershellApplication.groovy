import com.fxinnovation.nexus.PowershellApplicationRepository

def call(Map config = [:], Map closures =[:]){
  fxJob([
    pipeline: { Map scmInfo ->
    
      printDebug('----- fxPowershellApplication -----')

      mapAttributeCheck(config, 'applicationName', CharSequence, '',  'You need to privide an applicationName')

      config.applicationName = config.applicationName.toLowerCase()

      def repository
      if (  'master' == scmInfo.branch && '' != scmInfo.tag ){
        config['version'] = scmInfo.tag
        repository = PowershellApplicationRepository.getReleaseRepository(this, config)
      }
      else{
        config['version'] = "latest-${scmInfo.branch.replace("\\", "-").replace("/", "-").replace(" ", "_").toLowerCase()}"
        repository = PowershellApplicationRepository.getUnstableRepository(this, config)
      }

      currentBuild.displayName = "#${BUILD_NUMBER} - ${config.version}"

      config['artefactFolder'] = "_artefacts"

      printDebug('----- Configs parsed -----')
     
      if (closures.containsKey('preBuild') && closures.preBuild instanceof Closure){
        stage('preBuild'){
          closures.preBuild()
        }
      }
      stage('build'){
        if(config.dependencies){
          printDebug("----- Dependencies found -----")
          execute(script: "cp -rpv src _backupsrc")
          for (dependency in config.dependencies) {
            def item = PowershellApplicationRepository.getItemFromMap(this, dependency)

            printDebug("----- adding dependency '${dependency.applicationName}' version: '${dependency.version}'-----")
            def zipfilePath = item.getFile(this,"_downloads")
            unzip (
              dir: '_repository', 
              glob: '', 
              zipFile: zipfilePath
            )

            execute(script: "cp -rpvf _repository/src/* src")
            execute(script: "rm -rf _repository")
          }

          execute(script: "cp -rpvf _backupsrc/* src")
        }

        printDebug('----- Building -----')
        powershellModule.buildApplication(config)
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

        def item = repository.newItem(this, config)

        def zips = findFiles(glob: '_artefacts/*.zip')
        if(zips.length == 0){
          throw new Exception("No files have been found to publish.")
        }
        item.publish(this,zips.path)

        currentBuild.description = "Artefact is available at \n${item.getUrl()}"

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