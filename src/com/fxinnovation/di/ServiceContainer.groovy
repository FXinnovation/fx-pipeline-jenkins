package com.fxinnovation.di

import com.fxinnovation.di.IOC
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDispatcher
import groovy.lang.Script
import com.fxinnovation.observer.EventListenerBag
import org.apache.groovy.json.internal.IO

class ServiceContainer {
  public Object get(string className) {
    return IOC.resolve(className)
  }

  void registerAllClasses(Script context) {
    IOC.registerSingleton(Debugger.class.getName(), function(context) {
      return new Debugger(context)
    })

    IOC.register(EventListenerBag.class.getName(), function() {
      return new EventListenerBag()
    })

    IOC.registerSingleton(EventDispatcher.class.getName(), function() {
      return new EventListenerBag(IOC.resolve(EventListenerBag.class.getName()))
    })
  }
}
