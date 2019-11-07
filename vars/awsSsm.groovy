import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.type.FxString

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
  mapAttributeCheck(config, 'type', CharSequence, '', 'ERROR: You must define a type.')
  mapAttributeCheck(config, 'overwrite', Boolean, false, '')
  mapAttributeCheck(config, 'value', CharSequence, '', 'ERROR: You must define a value.')

  if (('String' != config.type) && ('StringList' != config.type) && ('SecureString' != config.type)){
    error('Parameter "type" must be one of ["String", "StringList", "SecureString"]')
  }

  def optionStringFactory = new OptionStringFactory(this)

  def optionsString = optionStringFactory.createOptionString(' ')

  if (config.type == 'SecureString'){
    mapAttributeCheck(config, 'keyId', CharSequence, '', 'ERROR: You must define a keyId.')
    optionStringFactory.addOption('--key-id', config.keyId)
  }

  if (config.overwrite){
    optionStringFactory.addOption('--overwrite')
  }

  withEnv([
      "SSM_PARAM_SECRET=${config.value}"
  ])
  {
    return execute(
      script: "aws ssm put-parameter --name '${new FxString(config.name).escapeBashSimpleQuote()}' --value \$SSM_PARAM_SECRET --type '${new FxString(config.type).escapeBashSimpleQuote()}' ${optionStringFactory.getOptionString().toString()}"
    )
  }
}
