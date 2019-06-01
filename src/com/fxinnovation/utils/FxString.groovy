package com.fxinnovation.utils

/**
 * String decorator
 */
class FxString implements Serializable, CharSequence, Comparable<String> {
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
    return this.innerString."${methodName}"(args)
  }

  /**
   * Returns the inner String transformed into CamelCase
   * @return String
   */
  String toCamelCase() {
    return this.innerString.split(/[^\w]/).collect { it.toLowerCase().capitalize() }.join('')
  }
}
