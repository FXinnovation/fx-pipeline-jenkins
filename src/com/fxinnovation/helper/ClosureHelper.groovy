package com.fxinnovation.helper

import groovy.lang.Script

class ClosureHelper {
  private Script context
  private Map closures

  ClosureHelper(Script context, Map closures) {
    this.closures = closures
    this.context = context
  }

  Boolean isDefined(String closureName) {
    return this.closures.containsKey(closureName) && Closure.isInstance(this.closures[closureName])
  }

  void throwErrorIfNotDefined(String closureName) {
    if (!this.isDefined(closureName)) {
      this.context.error("Closure “${closureName}” is mandatory !")
    }
  }

  public execute(String closureName, ...args) {
    if(this.isDefined(closureName)) {
      if ([] != args) {
        this.context.println(
          "WARNING: some variables were passed to closure “${closureName}”, this will probably create an error. " +
          'As today (2020-01) CPS workflow does not support spread syntax: “spread not yet supported for CPS transformation” (*args). ' +
          "Therefore, passing arguments to closures using the “${this.getClass()}” is not yet supported. " +
          "To solve this issue either remove arguments from closure call or call the closure without using the “${this.getClass()}” object."
        )
      }
      return this.closures[closureName]()
    }
  }

  public executeWithinStage(String closureName, ...args) {
    if (this.isDefined(closureName)) {
      this.context.stage(closureName) {
        return this.execute(closureName, args)
      }
    }
  }

  Map getClosures() {
    return closures
  }

  void addClosure(String closureName, Closure closure) {
     if('' == closureName) {
       this.context.println("“closureName” cannot be empty")
       return
     }

     this.closures[closureName] = closure
  }

  void addClosureOnlyIfNotDefined(String closureName, Closure closure) {
     if(this.isDefined(closureName)) {
       this.context.printDebug("“" + closureName + "” is already defined.")
       return
     }

     this.addClosure(closureName, closure)
  }
}
