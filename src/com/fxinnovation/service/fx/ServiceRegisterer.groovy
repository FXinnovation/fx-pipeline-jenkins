package com.fxinnovation.service.fx

import com.fxinnovation.di.IOC
import com.fxinnovation.listener.fx.*

class ServiceRegisterer {
  static alreadyRegistered = false

  void registerAllClasses() {
    if (true == this.alreadyRegistered) {
      return
    }

    this.registerListeners()

    this.alreadyRegistered = true
  }

  void registerListeners(Script context) {
    IOC.registerSingleton(TerraformPrepareSSHForInitListener.class.getName(), {
      return new TerraformPrepareSSHForInitListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformRepositoryNameStandardListener.class.getName(), {
      return new TerraformRepositoryNameStandardListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformFileStandardListener.class.getName(), {
      return new TerraformFileStandardListener(IOC.get('@context'))
    })
  }
}
