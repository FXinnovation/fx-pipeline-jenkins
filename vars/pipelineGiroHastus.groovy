fxJob([
  pipeline: { Map scmInfo ->
    def listCustomer = execute (
      script: "ls -d */ |sed -e 's/\\///g'"
    )

    fx_notify(
      status: 'PENDING'
    )

    customer = input(
      message : 'Which customer\'s infrastructure ?',
      ok: 'Select',
      parameters: [choice(name: 'customer', choices: listCustomer.stdout, description: 'What is the customer?')]
    )

    def versionFileExists = fileExists("${customer}/version.yml")
    def manifestFileExists = fileExists("${customer}/manifest.xml")

    def isTagged = '' != scmInfo.tag
    def isMaster = 'master' == scmInfo.branch
    def publish = false

    if (isTagged && isMaster) {
      publish = true
    }

    if (!versionFileExists) {
      error("File \"${customer}/version.yml\" must exist")
    }
    if (!manifestFileExists) {
      error("File \"${customer}/manifest.xml\" must exist")
    }

    def versions = readYaml(
      file: "${customer}/version.yml"
    )

    if (null == versions['fxinnovation-common-scripts-powershell']) {
      error("\"fxinnovation-common-scripts-powershell\" version must be set in file \"${customer}/version.yml\"")
    }
    if (null == versions['giro-cloud-orchestration']) {
      error("\"giro-cloud-orchestration\" version must be set in file \"${customer}/version.yml\"")
    }
    if (null == versions['cookbook-hastus']) {
      error("\"cookbook-hastus\" version must be set in file \"${customer}/version.yml\"")
    }
    if (null == versions['clientNumber']) {
      error("\"clientNumber\" version must be set in file \"${customer}/version.yml\"")
    }

    // These files should be hosted in Nexus once it is setup, this is ugly
    dir('fxinnovation-common-scripts-powershell') {
      git(
        credentialsId: 'jenkins_fxinnovation_bitbucket',
        changelog: false,
        poll: false,
        url: 'https://bitbucket.org/fxadmin/fxinnovation-common-scripts-powershell.git'
      )

      tagExist = execute (
        script: "git rev-parse -q --verify \"refs/tags/${versions['fxinnovation-common-scripts-powershell']}\"",
        throwError: false
      )

      if ("" == tagExist.stdout) {
        error("There is no tag \"${versions['fxinnovation-common-scripts-powershell']}\" in the repo \"fxinnovation-common-scripts-powershell\"")
      }

      execute (
        script: "git checkout ${versions['fxinnovation-common-scripts-powershell']}"
      )
    }

    dir('giro-cloud-orchestration') {
      git(
        credentialsId: 'jenkins_fxinnovation_bitbucket',
        changelog: false,
        poll: false,
        url: 'https://bitbucket.org/fxadmin/giro-cloud-orchestration.git'
      )

      tagExist = execute (
        script: "git rev-parse -q --verify \"refs/tags/${versions['giro-cloud-orchestration']}\"",
        throwError: false
      )

      if ("" == tagExist.stdout) {
        error("There is no tag \"${versions['giro-cloud-orchestration']}\" in the repo \"giro-cloud-orchestration\"")
      }

      execute (
        script: "git checkout ${versions['giro-cloud-orchestration']}"
      )
    }

    dir('cookbook-hastus') {
      git(
        credentialsId: 'gitea-administrator',
        changelog: false,
        poll: false,
        url: 'https://scm.dazzlingwrench.fxinnovation.com/giro/cookbook-hastus.git'
      )

      tagExist = execute (
        script: "git rev-parse -q --verify \"refs/tags/${versions['cookbook-hastus']}\"",
        throwError: false
      )

      if ("" == tagExist.stdout) {
        error("There is no tag \"${versions['cookbook-hastus']}\" in the repo \"cookbook-hastus\"")
      }
    }

    execute (
      script: "rm -rf cookbook-hastus"
    )

    sh 'mkdir -p output'
    
    execute (
      script: 'chmod o+w output'
    )

    withCredentials([
      usernamePassword(
        credentialsId: 'giro-service-principal',
        usernameVariable: 'appid',
        passwordVariable: 'servicePrincipalPassword'
      )
    ]) {
      executePowershell([
        script: "/data/giro-cloud-orchestration/ManifestReader/Export-AzureStackDeployer.ps1 -InputXmlFile \"/data/${customer}/manifest.xml\" -InputCustomerConfigFile \"/data/giro-cloud-orchestration/ManifestReader/Input-Test/Giro-config.json\" -ModuleFxLocation \"/data/fxinnovation-common-scripts-powershell/PsModules/FXNameStandard/1.0\" -OutputPath \"/data/output\" -Tenant \"5748501a-0f16-478b-a990-e53164e32fa8\" -AppId \"${appid}\"  -Local -Verbose",
        dockerEnvironmentVariables: [
          Password: "${servicePrincipalPassword}"
        ]
      ])
    }

    giroFxClientName = executePowershell([
        script: "/data/giro-cloud-orchestration/ManifestReader/Pipeline/GiroFxClientName.ps1 -ModulePath \"/data/giro-cloud-orchestration/ManifestReader/AzureStackDeployerGenerator\" -XmlFilePath \"/data/${customer}/manifest.xml\""
    ])

    println "GiroFxClientName : ${giroFxClientName.stdout}"

    withCredentials([
      string(
        credentialsId: 'giro-sas-key-blob-storage',
        variable: 'sas_key'
      )
    ]) {
      executePowershell([
        script: "/data/giro-cloud-orchestration/ManifestReader/Pipeline/UploadBlobStorage.ps1 -StorageAccountName \"girozca1pgensa000\" -ContaineName \"aa-inputfiles\" -LocalPath \"/data/output/\" -RemotePath \"${giroFxClientName.stdout}\" -SasToken \"${sas_key}\" "
      ])
    }

    if (!publish) {
      println "===================\nThis is not a tagged version, this pipeline will not deploy\n==================="
      return
    }

    fx_notify(
      status: 'PENDING'
    )
    input 'WARNING: You are about to deploy. Do you want to apply it?'
  },
],
[
  disableConcurrentBuilds(),
  buildDiscarder(
    logRotator(
      artifactDaysToKeepStr: '',
      artifactNumToKeepStr: '10',
      daysToKeepStr: '',
      numToKeepStr: '10'
    )
  ),
  pipelineTriggers([
  [
    $class  : 'PeriodicFolderTrigger',
    interval: '1d'
  ]])
])
