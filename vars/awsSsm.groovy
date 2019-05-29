import com.fxinnovation.utils.OptionString

def getParameter(Map config = [:]) {
  mapAttributeCheck(config, 'name', CharSequence, '', '')
  mapAttributeCheck(config, 'withDescription', Boolean, false, '')

  if (config.withDescription) {
    def option = '--with-decryption'
  }else{
    def option = '--no-with-decryption'
  }

  return readJSON(
    text: execute(
      script: "aws ssm get-parameter --name ${config.name} ${config.option}"
    ).stdout()
  ).Parameter
}

def putParameter(Map config = [:]) {
  mapAttributeCheck(config, 'name', CharSequence, 'ERROR: You must define the parameter name.')
  mapAttributeCheck(config, 'value', CharSequence, '', 'ERROR: You must define a value.')
  mapAttributeCheck(config, 'type', CharSequence, '', 'ERROR: You must define a type.')
  mapAttributeCheck(config, 'overwrite', Boolean, false, '')

  def optionsString = new OptionString(this)
  optionsString.setDelimiter(' ')

  if ((config.type != 'String') && (config.type != 'StringList') && (config.type != 'SecureString')){
    error('Parameter "type" must be one of ["String", "StringList", "SecureString"]')
  }
  if (config.type == 'SecureString'){
    mapAttributeCheck(config, 'keyId', CharSequence, '', 'ERROR: You must define a keyId.')
    optionsString.add('--key-id', config.keyId)
  }

  if (config.overwrite){
    optionsString.add('--overwrite')
  }

  return execute(
    script: "aws ssm put-parameter --name ${config.name} --value ${config.value} --type ${config.type} ${optionsString}"
  )
}
