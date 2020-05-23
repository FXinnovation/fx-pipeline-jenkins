package com.fxinnovation.service.fx

import com.fxinnovation.di.IOC
import com.fxinnovation.listener.fx.*
import com.fxinnovation.io.Debugger

class ServiceRegisterer {
  static alreadyRegistered = false

  void registerAllClasses() {
    if (true == this.alreadyRegistered) {
      return
    }

    this.registerListeners()

    this.alreadyRegistered = true
  }

  void registerListeners() {
    IOC.registerSingleton(TerraformPrepareSSHForInitListener.class.getName(), {
      return new TerraformPrepareSSHForInitListener(IOC.get('@context'), IOC.get(Debugger.class.getName()))
    })
    IOC.registerSingleton(TerraformCleanSSHForInitListener.class.getName(), {
      return new TerraformPrepareSSHForInitListener(IOC.get('@context'), IOC.get(Debugger.class.getName()))
    })
    IOC.registerSingleton(TerraformRepositoryNameStandardListener.class.getName(), {
      return new TerraformRepositoryNameStandardListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformFileStandardListener.class.getName(), {
      return new TerraformFileStandardListener(IOC.get('@context'))
    })
  }
}
