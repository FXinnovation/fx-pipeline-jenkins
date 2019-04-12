def call(Map config = [:]) {
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
//    properties(defaultPropertiesConfig)
    status = 'SUCCESS'
    def label = UUID.randomUUID().toString()
    def label2 = UUID.randomUUID().toString()
    def label3 = UUID.randomUUID().toString()

    podTemplate(
            cloud: 'kubernetes',
            name: 'jenkins-slave-linux',
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
    ) {
        node(label) {
            try {
                ansiColor('xterm') {

                    parallel lint: {
                        node(label2) {
                            fxCheckout()
                            pipelinePython([
                                    version: config.version,
                                    stage  : 'lint'
                            ])
                        }
                    }, test: {
                        node(label3) {
                            fxCheckout()
                            pipelinePython([
                                    version: config.version,
                                    stage  : 'test'
                            ])
                        }
                    }
                }
            } catch (error) {
                status = 'FAILURE'
                throw error
            } finally {
                stage('notify') {
                    if (closures.containsKey('preNotify')) {
                        closures.preNotify()
                    }
                    fx_notify(
                            status: status
                    )
                    if (closures.containsKey('postNotify')) {
                        closures.postNotify()
                    }
                }
                stage('cleanup') {
                    if (closures.containsKey('preCleanup')) {
                        closures.preCleanup()
                    }
                    cleanWs()
                    if (closures.containsKey('postCleanup')) {
                        closures.postCleanup()
                    }
                }
            }
        }
    }
}
