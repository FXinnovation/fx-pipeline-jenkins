package com.fxinnovation.di

import com.fxinnovation.di.IOC
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDispatcher
import groovy.lang.Script
import com.fxinnovation.observer.EventListenerBag

class ServiceContainer {
  static alreadyRegister = false

  void registerAllClasses(Script context) {
    IOC.registerSingleton(Debugger.class.getName(),{
      return new Debugger(context)
    })

    IOC.register(EventListenerBag.class.getName(), {
      return new EventListenerBag()
    })

    IOC.registerSingleton(EventDispatcher.class.getName(), {
      return new EventListenerBag(IOC.resolve(EventListenerBag.class.getName()))
    })
  }
}
