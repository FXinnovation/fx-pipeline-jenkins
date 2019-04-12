def call(Map closures = [:], List propertiesConfig = []){
    mapAttributeCheck(config, 'version', CharSequence, '3')

    defaultPropertiesConfig = [
            buildDiscarder(
                    logRotator(
                            artifactDaysToKeepStr: '',
                            artifactNumToKeepStr: '10',
                            daysToKeepStr: '',
                            numToKeepStr: '10'
                    )
            ),
            pipelineTriggers(
                    [cron('@midnight')]
            )
    ]

//    properties(defaultPropertiesConfig + propertiesConfig)
    status='SUCCESS'
    def label2 = UUID.randomUUID().toString()
    podTemplate(
            cloud: 'kubernetes',
            name:  'jenkins-slave-linux',
            namespace: 'default',
            nodeUsageMode: 'NORMAL',
            idleMinutes: 0,
            slaveConnectTimeout: 100,
            podRetention: never(),
            label: label2,
            containers: [
                    containerTemplate(
                            name: 'jnlp',
                            image: "fxinnovation/jenkinsk8sslave:latest",
                            args: '${computer.jnlpmac} ${computer.name}',
                            privileged: true,
                            alwaysPullImage: true,
                            workingDir: '/home/jenkins',
                            resourceRequestCpu: '100m',
                            resourceLimitCpu: '1',
                            resourceRequestMemory: '1024Mi',
                            resourceLimitMemory: '2048Mi'
                    )
            ]
    )
    def label = UUID.randomUUID().toString()
    podTemplate(
            cloud: 'kubernetes',
            name:  'jenkins-slave-linux',
            namespace: 'default',
            nodeUsageMode: 'NORMAL',
            idleMinutes: 0,
            slaveConnectTimeout: 100,
            podRetention: never(),
            label: label,
            containers: [
                    containerTemplate(
                            name: 'jnlp',
                            image: "fxinnovation/jenkinsk8sslave:latest",
                            args: '${computer.jnlpmac} ${computer.name}',
                            privileged: true,
                            alwaysPullImage: true,
                            workingDir: '/home/jenkins',
                            resourceRequestCpu: '100m',
                            resourceLimitCpu: '1',
                            resourceRequestMemory: '1024Mi',
                            resourceLimitMemory: '2048Mi'
                    )
            ]
    ){
        node(label){
            try{
                ansiColor('xterm') {
                    stage('prepare'){
                        if (closures.containsKey('prePrepare')){
                            closures.prePrepare()
                        }
                        if (closures.containsKey('postPrepare')){
                            closures.postPrepare()
                        }
                    }
                    parallel lint: {
                        node(label1) {
                            fxCheckout()
                            pipelinePython([
                                    version: config.version,
                                    stage: 'lint'
                            ])
                        }
                    },
                    test: {
                        fxCheckout()
                        pipelinePython([
                                version: config.version,
                                stage: 'test'
                        ])
                    }
                }
            }catch(error){
                status='FAILURE'
                throw error
            }finally{
                stage('notify'){
                    if (closures.containsKey('preNotify')){
                        closures.preNotify()
                    }
                    fx_notify(
                            status: status
                    )
                    if (closures.containsKey('postNotify')){
                        closures.postNotify()
                    }
                }
                stage('cleanup'){
                    if (closures.containsKey('preCleanup')){
                        closures.preCleanup()
                    }
                    cleanWs()
                    if (closures.containsKey('postCleanup')){
                        closures.postCleanup()
                    }
                }
            }
        }
    }
}
