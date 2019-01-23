
SSHHostKeys = ['1', '2']

  sshAgentComment = "eval \$(ssh-agent -s) && ssh-add " + SSHHostKeys.join(" && ssh-add ")

print(sshAgentComment)
