/****************************************
// This file should not be used anymore
// Obsolete
*****************************************/

import com.fxinnovation.data.ScmInfo

def call(Map config = [:]) {
  fxJob([
    pipeline: { ScmInfo scmInfo ->
      def listCustomer = execute (
        script: "ls -d */ |sed -e 's/\\///g'"
      )

      fx_notify(
        status: 'PENDING'
      )

      timeout(time: 10, unit: 'MINUTES') {
        customer = input(
          message : 'Which customer\'s infrastructure ?',
          ok: 'Select',
          parameters: [choice(name: 'customer', choices: listCustomer.stdout, description: 'What is the customer?')]
        )
      }

      def versionFileExists = fileExists("${customer}/version.yml")
      def manifestFileExists = fileExists("${customer}/manifest.xml")

      def isTagged = '' != scmInfo.getTag()
      def isMaster = 'master' == scmInfo.getBranch()
      def publish = false

      if (isTagged && isMaster) {
        publish = true
      }

      if ('tst' == scmInfo.getBranch()) {
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
      fxCheckoutTag (
        directory: 'fxinnovation-common-scripts-powershell',
        credentialsId: 'jenkins_fxinnovation_bitbucket',
        repoUrl: 'https://bitbucket.org/fxadmin/fxinnovation-common-scripts-powershell.git',
        tag: versions['fxinnovation-common-scripts-powershell']
      )
      fxCheckoutTag (
        directory: 'giro-cloud-orchestration',
        credentialsId: 'jenkins_fxinnovation_bitbucket',
        repoUrl: 'https://bitbucket.org/fxadmin/giro-cloud-orchestration.git',
        tag: versions['giro-cloud-orchestration']
      )
      fxCheckoutTag (
        directory: 'cookbook-hastus',
        credentialsId: 'gitea-administrator',
        repoUrl: 'https://scm.dazzlingwrench.fxinnovation.com/giro/cookbook-hastus.git',
        tag: versions['cookbook-hastus']
      )
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
          script: "/data/giro-cloud-orchestration/ManifestReader/Export-AzureStackDeployer.ps1 -InputXmlFile \"/data/${customer}/manifest.xml\" -InputCustomerConfigFile \"/data/giro-cloud-orchestration/ManifestReader/Input-Test/Giro-config.json\" -ModuleFxLocation \"/data/fxinnovation-common-scripts-powershell/PsModules/FXNameStandard/1.0\" -OutputPath \"/data/output\" -Tenant \"5748501a-0f16-478b-a990-e53164e32fa8\" -AppId \"${appid}\" -TagVersion \"${versions['giro-cloud-orchestration']}\" -ClientNumber \"${versions['clientNumber'].toString().padLeft(2,'0')}\" -Local -Verbose",
          dockerEnvironmentVariables: [
            Password: "${servicePrincipalPassword}"
          ]
        ])
      }

      giroFxClientName = executePowershell([
          script: "/data/giro-cloud-orchestration/ManifestReader/Pipeline/GiroFxClientName.ps1 -ModulePath \"/data/giro-cloud-orchestration/ManifestReader/AzureStackDeployerGenerator\" -XmlFilePath \"/data/${customer}/manifest.xml\""
      ])

      fxAzureUploadBlob(
        credentialSasKey: 'giro-sas-key-blob-storage',
        storageAccountName: 'girozca1pgensa000',
        containerName: 'aa-inputfiles',
        localFilePath: 'output',
        blobFilePath: giroFxClientName.stdout,
        deleteBeforeUpload: true,
        filter: '*.txt',
        libFolder: 'bar'
      )

      def nodes = []
      def location = ''
      def environment = ''

      def listNodes = execute (
        script: "ls output/*.txt | sed -e 's/\\.txt\$//g' |sed -e 's/^output\\///g'"
      )

      for (node in listNodes.stdout.split()) {

        parsingNode = node.split(/\./)

        if('stg' != parsingNode[3]) {
          def nodeDetails = [:]
          nodeDetails.put('name', parsingNode[2])
          nodeDetails.put('role', parsingNode[3])

          nodes.add(nodeDetails)

          environment = parsingNode[0]
          location = parsingNode[1]
        }
      }

      if (!publish) {
        println "===================\nThis is not a tagged version, this pipeline will not deploy\n==================="
        return
      }

      fx_notify(
        status: 'PENDING'
      )
      timeout(time: 10, unit: 'MINUTES') {
        input 'WARNING: You are about to deploy. Do you want to apply it?'
      }

      fxAzureRunRunbook(
        credentialAzure: 'giro-service-principal',
        resourceGroupName: 'girozca1pgenrg000',
        runbook: 'Run-Orchestrator',
        automationAccountName: 'girozca2pgenaa000',
        tenantId: '5748501a-0f16-478b-a990-e53164e32fa8',
        runbookOptions: "CLIENTNAME=${giroFxClientName.stdout};ENVIRONMENT=${environment};LOCATION=${location};RESOURCEGROUPNUMBER=${versions['clientNumber'].toString().padLeft(2,'0').padRight(3,'1')};CURRENTDEPLOYMENTTYPE=app,sql;TAGVERSION=${versions['giro-cloud-orchestration']}"
      )
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
}
