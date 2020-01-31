package com.fxinnovation.io

import groovy.lang.Script

class Debugger {
  private Script context

  Debugger(Script context) {
    this.context = context
  }

  /**
   * Prints a content only if the debug exists.
   */
  void printDebug(Object content) {
    if (this.debugVarExists()) {
      this.context.println('### DEBUG ###')
      this.context.println(content)
      this.context.println('### END DEBUG ###')
    }
  }

  /**
   * Checks whether or not the debug variable is set.
   */
  Boolean debugVarExists() {
    // The silent try/catch simulate a check “is_variable_defined” that does not exist in Groovy
    try {
      return null != this.context.env.DEBUG
    } catch (MissingPropertyException missingPropertyException) {}

    return false
  }
}
