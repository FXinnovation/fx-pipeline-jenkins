import com.fxinnovation.utils.OptionString
import com.fxinnovation.utils.FxString

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
      script: "aws ssm get-parameter --name '${new FxString(config.name).escapeBashSimpleQuote()}' ${config.option}"
    ).stdout()
  ).Parameter
}

def putParameter(Map config = [:]) {
  mapAttributeCheck(config, 'name', CharSequence, 'ERROR: You must define the parameter name.')
  mapAttributeCheck(config, 'value', hudson.util.Secret, '', 'ERROR: You must define a value.')
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
    script: "aws ssm put-parameter --name '${new FxString(config.name).escapeBashSimpleQuote()}' --value '${new FxString(config.value.getPlainText()).escapeBashSimpleQuote()}' --type '${new FxString(config.type).escapeBashSimpleQuote()}' ${optionsString.toString()}"
  )
}
