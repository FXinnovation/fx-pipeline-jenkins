@NonCPS
def call(Object message){
  // The try/catch with silent catch is to simulate a check “is_variable_defined” that does not exist in groovy
  try {
    if (env.DEBUG != null) {
      println(message)
    }
  } catch (MissingPropertyException missingPropertyException) {}
}
