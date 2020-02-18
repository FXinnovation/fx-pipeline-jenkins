package com.fxinnovation.di

import com.fxinnovation.di.IOC
import com.fxinnovation.io.Debugger
import com.fxinnovation.observer.EventDispatcher
import groovy.lang.Script
import com.fxinnovation.observer.EventListenerBag
import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.deprecation.DeprecatedMessage

class ServiceRegisterer {
  static alreadyRegister = false

  void registerAllClasses(Script context) {
    if (true == this.alreadyRegister) {
      return
    }

    this.registerObserver()
    this.registerDebugger(context)
    this.registerFactories(context)
    this.registerDeprecation(context)

    this.alreadyRegister = true
  }

  void registerDebugger(Script context) {
    IOC.registerSingleton(Debugger.class.getName(),{
      return new Debugger(context)
    })
  }

  void registerFactories(Script context) {
    IOC.register(OptionStringFactory.class.getName(), {
      return new OptionStringFactory(context)
    })
  }

  void registerObserver() {
    IOC.register(EventListenerBag.class.getName(), {
      return new EventListenerBag()
    })

    IOC.registerSingleton(EventDispatcher.class.getName(), {
      return new EventListenerBag(IOC.get(EventListenerBag.class.getName()))
    })
  }

  void registerDeprecation(Script context) {
    IOC.registerSingleton(DeprecatedMessage.class.getName(), {
      return new DeprecatedMessage(context)
    })

    IOC.registerSingleton(DeprecatedFunction.class.getName(), {
      return new DeprecatedFunction(context, IOC.get(DeprecatedMessage.class.getName()))
    })
  }
}
