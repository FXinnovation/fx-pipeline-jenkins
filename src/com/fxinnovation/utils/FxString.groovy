package com.fxinnovation.utils

/**
 * String decorator
 */
class FxString implements Serializable {
  private String innerString

  FxString(String innerString) {
    this.innerString = innerString
  }

  /**
   * Delegates unknown method call to the inner string object
   * @param methodName
   * @param args
   */
  def methodMissing(String methodName, args) {
    this.innerString."${methodName}"(args)
  }

  /**
   * Converts the inner string into camel case
   * @param userInputs
   * @param option Whether or not to: trim the result of the default values, convert values with EOL to list, etc.
   * @return Map
   */
  String toCamelCase() {
    return this.innerString.split(/[^\w]/).collect { it.toLowerCase().capitalize() }.join("")
  }
}
