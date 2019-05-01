def assumeRole(String awsRoleArn, Closure body){
  def roleSession = "jenkins_${JOB_BASE_NAME}_${BUILD_ID}".replaceAll("%", "-")
  awsSessionJson = execute(
    script: "aws sts assume-role \
             --role-arn ${awsRoleArn} \
             --duration-seconds 3600 \
             --role-session-name ${roleSession}"
  ).stdout
  def awsSessionObject = readJSON text: awsSessionJson
  withEnv([
    "AWS_SESSION_TOKEN=${awsSessionObject.Credentials.SessionToken}",
    "AWS_ACCESS_KEY_ID=${awsSessionObject.Credentials.AccessKeyId}",
    "AWS_SECRET_ACCESS_KEY=${awsSessionObject.Credentials.SecretAccessKey}"
  ]){
    body()
  }
}
