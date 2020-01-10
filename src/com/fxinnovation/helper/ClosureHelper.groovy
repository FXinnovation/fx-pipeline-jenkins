package com.fxinnovation.helper

class ClosureHelper {
  private Map closures

  ClosureHelper(Map closures) {
    this.closures = closures
  }

  Boolean isDefined(String closureName) {
    return this.closures.containsKey(closureName) && Closure.isInstance(this.closures[closureName])
  }

  public execute(String closureName, ...args) {
    return this.closures[closureName](*args)
  }

  public executeWithinStage(String closureName, ...args) {
    if (this.isDefined(closureName)) {
      stage(closureName) {
        return this.execute(closureName, *args)
      }
    }
  }

  Map getClosures() {
    return closures
  }
}
