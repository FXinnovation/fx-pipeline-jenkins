package com.fxinnovation.di

import com.fxinnovation.io.Debugger
import com.fxinnovation.listener.standard.TerraformInitListener
import com.fxinnovation.observer.EventDispatcher
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
    this.registerListeners(context)
    this.registerDebugger(context)
    this.registerFactories(context)
    this.registerDeprecation(context)

    this.alreadyRegister = true
  }

  void registerDebugger(Script context) {
    IOC.registerSingleton(Debugger.class.getName(), {
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
      return new EventDispatcher(IOC.get(EventListenerBag.class.getName()))
    })
  }

  void registerListeners(Script context) {
    IOC.registerSingleton(TerraformInitListener.class.getName(), {
      return new TerraformInitListener(context)
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
