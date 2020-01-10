package com.fxinnovation.helper

class ClosureHelper {
  private Map closures

  ClosureHelper(Map closures) {
    this.closures = closures
  }

  Boolean isDefined(String closureName) {
    return this.closures.containsKey(closureName) && Closure.isInstance(this.closures[closureName])
  }

  /**
   * Executes a closure. NOT USABLE WITH JENKINS SECURITY SETTINGS BY DEFAULT.
   */
  public execute(String closureName, ...args) {
    // As today (2020-01) CPS does not support spread: “spread not yet supported for CPS transformation”.
    // Therefore, passing variables to closure is not yet supported. Arguments passed to closure below should be “*args” (works in vanilla groovy).
    return this.closures[closureName]()
  }

  /**
   * Executes a closure. NOT USABLE WITH JENKINS SECURITY SETTINGS BY DEFAULT.
   */
  public executeWithinStage(String closureName, ...args) {
    if (this.isDefined(closureName)) {
      stage(closureName) {
        // @See the comment at the execute method of this object. Arguments passed to closure below should be “*args” (works in vanilla groovy).
        return this.execute(closureName, args)
      }
    }
  }

  Map getClosures() {
    return closures
  }
}
