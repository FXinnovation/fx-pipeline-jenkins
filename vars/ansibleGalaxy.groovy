def install(Map config = [:]) {
  mapAttributeCheck(config, 'reqFile',   CharSequence, 'requirements.yml')
  mapAttributeCheck(config, 'rolesPath', CharSequence, 'roles/')

  config.options = "install --role-file=${config.reqFile} --roles-path=${config.rolesPath}"

  ansibleGalaxy(config)
}

def call(Map config = [:]){
  mapAttributeCheck(config, 'dockerImage',    CharSequence, 'fxinnovation/ansible:latest')
  mapAttributeCheck(config, 'options',        CharSequence, '')
  mapAttributeCheck(config, 'sshHostKeys',    List,         [])
  mapAttributeCheck(config, 'sshAgentSocket', CharSequence, '')

  def additionalMounts = [:]
  def environmentVariables = [:]

  if (0 != config.sshHostKeys.size) {
    sh('mkdir -p ~/.ssh')
    sh('echo "' + config.sshHostKeys.join('" >> ~/.ssh/known_hosts && echo "') + '" >> ~/.ssh/known_hosts')
    additionalMounts << [ '~/.ssh/known_hosts': '/root/.ssh/known_hosts' ]
  }

  if (0 != config.sshAgentSocket.length()) {
    additionalMounts << [ '\$(readlink -f $SSH_AUTH_SOCK)': '/ssh-agent' ]
    environmentVariables << [ 'SSH_AUTH_SOCK': '/ssh-agent' ]
  }

  def ansibleGalaxyCommand = dockerRunCommand(
    dockerImage:          config.dockerImage,
    fallbackCommand:      'ansible-galaxy',
    command:              'ansible-galaxy',
    additionalMounts:     additionalMounts,
    environmentVariables: environmentVariables
  )

  execute(
    script: "${ansibleGalaxyCommand} --version"
  )

  return execute(
    script: "${ansibleGalaxyCommand} ${config.options}"
  )
}
