def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage', CharSequence, 'fxinnovation/ansible-playbook:latest')
  mapAttributeCheck(config, 'options', CharSequence, '')
  mapAttributeCheck(config, 'commandTarget', CharSequence, '.')

  def ansiblePlaybookCommand = dockerRunCommand(
    dockerImage: config.dockerImage,
    fallbackCommand: 'ansible-playbook',
    command: 'ansible-playbook'
  )

  execute(
    script: "${ansiblelintCommand} --version"
  )

  return execute(
    script: "${ansiblelintCommand} ${config.options} ${config.commandTarget}"
  )
}
