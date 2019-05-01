def getLogin () {
  return execute(
    script: 'aws ecr get-login'
  )
}
