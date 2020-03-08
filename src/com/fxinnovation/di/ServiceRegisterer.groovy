package com.fxinnovation.di

import com.fxinnovation.io.Debugger
import com.fxinnovation.listener.fx.*
import com.fxinnovation.listener.standard.*
import com.fxinnovation.observer.EventDispatcher
import com.fxinnovation.observer.EventListenerBag
import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.deprecation.DeprecatedMessage

class ServiceRegisterer {
  static alreadyRegistered = false

  void registerAllClasses(Script context) {
    if (true == this.alreadyRegistered) {
      return
    }

    this.registerDeprecation(context)
    this.registerDebugger(context)
    this.registerObserver()
    this.registerListeners(context)
    this.registerFactories(context)

    this.alreadyRegistered = true
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
      return new EventDispatcher(IOC.get(EventListenerBag.class.getName()), IOC.get(Debugger.class.getName()))
    })
  }

  void registerListeners(Script context) {
    IOC.registerSingleton(TerraformInitListener.class.getName(), {
      return new TerraformInitListener(context)
    })
    IOC.registerSingleton(TerraformValidateListener.class.getName(), {
      return new TerraformValidateListener(context)
    })
    IOC.registerSingleton(TerraformFmtListener.class.getName(), {
      return new TerraformFmtListener(context)
    })
    IOC.registerSingleton(TerraformPlanListener.class.getName(), {
      return new TerraformPlanListener(context)
    })
    IOC.registerSingleton(TerraformApplyListener.class.getName(), {
      return new TerraformApplyListener(context)
    })
    IOC.registerSingleton(TerraformPlanReplayListener.class.getName(), {
      return new TerraformPlanReplayListener(context)
    })
    IOC.registerSingleton(TerraformDestroyListener.class.getName(), {
      return new TerraformDestroyListener(context)
    })
    IOC.registerSingleton(TerraformArtifactCleanerListener.class.getName(), {
      return new TerraformArtifactCleanerListener(context)
    })
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
