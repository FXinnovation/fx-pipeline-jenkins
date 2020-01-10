package com.fxinnovation.helper

import groovy.lang.Script

class ClosureHelper {
  private Map closures
  private Script context

  ClosureHelper(Map closures) {
    this.closures = closures
    this.context = context
  }

  Boolean isDefined(String closureName) {
    return this.closures.containsKey(closureName) && Closure.isInstance(this.closures[closureName])
  }

  public execute(String closureName, ...args) {
    // As today (2020-01) CPS does not support spread: “spread not yet supported for CPS transformation”.
    // Therefore, passing variables to closure is not yet supported. Arguments passed to closure below should be “*args” (works in vanilla groovy).
    return this.closures[closureName]()
  }

  public executeWithinStage(String closureName, ...args) {
    if (this.isDefined(closureName)) {
      this.context.stage(closureName) {
        // @See the comment at the execute method of this object. Arguments passed to closure below should be “*args” (works in vanilla groovy).
        return this.execute(closureName, args)
      }
    }
  }

  Map getClosures() {
    return closures
  }
}
