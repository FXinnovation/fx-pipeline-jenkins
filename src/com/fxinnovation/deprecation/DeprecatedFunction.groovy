package com.fxinnovation.deprecation

import groovy.lang.Script

class DeprecatedFunction {
  private Script context
  private DeprecatedMessage deprecatedMessage

  DeprecatedFunction(Script context, DeprecatedMessage deprecatedMessage) {
    this.context = context
    this.deprecatedMessage = deprecatedMessage
  }

  public execute(Closure legacyClosure, String oldFunctionName, String newFunctionName, String deprecationDate) {
    def deprecation = new Date().parse("dd-MM-yyyy", deprecationDate)
    def curentDate = new Date()

    if(curentDate.after(deprecation)) {
      return this.deprecatedMessage.throwErrorDeletedFunction(oldFunctionName, newFunctionName, deprecationDate)
    }

    this.deprecatedMessage.displayWarningDeprecatedFunction(oldFunctionName, newFunctionName, deprecationDate)

    return legacyClosure()
  }
}