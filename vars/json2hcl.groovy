def call( Map config = [:] ){
  optionsString = ''
  if ( config.containsKey('reverse') ){
    if ( config.'reverse' instanceof Boolean ){
      if ( config.'reverse' ){
        optionsString = optionsString + '-reverse '
      }
    }else{
      error('json2hcl - reverse parameter must be of type "Boolean"')
    }
  }
  if ( !config.containsKey('input') ){
    error('json2hcl - input parameter is mandatory')
  }
  if ( !config.input instanceof String ){
    error('json2hcl - input parameter must be of type "String" ')
  }
  if ( !config.containsKey('output') ) {
    config.output = 'text'
  }
  if ( !config.containsKey('dockerImage') ) {
    config.dockerImage = 'fxinnovation/json2hcl:latest'
  }
  
  try {
    sh(
      returnStdout: true,
      script:       'docker version'
    )
    sh(
      returnStdout: true,
      script:       "docker pull ${config.dockerImage}"
    )
    execCommand = "docker run --rm -i ${config.dockerImage} "
  }catch(dockerError) {
    println 'Docker is not available, assuming json2hcl is installed'
    execCommand = 'json2hcl'
  }

  execVersion = sh(
    returnStdout: true,
    script:       "${execCommand} -version"
  )

  println "json2hcl version is:\n${execVersion}"

  text_output = sh(
    returnStdout: true,
    script:       "echo '${config.input}' | ${terraformCommand} ${optionsString}"
  ).trim()
  
  switch ( config.output ) {
    case 'text':
      return text_output
      break
    case 'object':
      return readJSON text: text_output
      break
    default:
      error("json2hcl - output parameter value '${config.output}' is not valid!")
      break
  }
}
