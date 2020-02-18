def call(Map config = [:]){
  registerServices()

  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/ansible:latest')
  mapAttributeCheck(config, 'options', CharSequence, '')
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')

  def ansiblePlaybookCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: 'ansible-playbook',
    command: 'ansible-playbook'
  )

  execute(
    script: "${ansiblePlaybookCommand} --version"
  )

  return execute(
    script: "${ansiblePlaybookCommand} ${config.options} ${config.commandTarget}"
  )
}
