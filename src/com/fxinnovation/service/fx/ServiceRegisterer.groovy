package com.fxinnovation.service.fx

import com.fxinnovation.di.IOC
import com.fxinnovation.listener.fx.*

class ServiceRegisterer {
  static alreadyRegistered = false

  void registerAllClasses(Script context) {
    if (true == this.alreadyRegistered) {
      return
    }

    this.registerListeners(context)

    this.alreadyRegistered = true
  }

  void registerListeners(Script context) {
    IOC.registerSingleton(TerraformInitListener.class.getName(), {
      return new TerraformInitListener(context)
    })
    IOC.registerSingleton(TerraformRepositoryNameStandardListener.class.getName(), {
      return new TerraformRepositoryNameStandardListener(context)
    })
    IOC.registerSingleton(TerraformFileStandardListener.class.getName(), {
      return new TerraformFileStandardListener(context)
    })
  }
}
