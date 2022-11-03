package com.fxinnovation.service.standard

import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.deprecation.DeprecatedMessage
import com.fxinnovation.di.IOC
import com.fxinnovation.factory.OptionStringFactory
import com.fxinnovation.helper.DockerRunnerHelper
import com.fxinnovation.io.Debugger
import com.fxinnovation.listener.standard.*
import com.fxinnovation.observer.EventDispatcher
import com.fxinnovation.observer.EventListenerBag

class ServiceRegisterer {
  static alreadyRegistered = false

  void registerAllClasses(Script context) {
    if (true == this.alreadyRegistered) {
      return
    }

    this.registerContext(context)
    this.registerDeprecation()
    this.registerDebugger()
    this.registerHelper()
    this.registerObserver()
    this.registerListeners()
    this.registerFactories()

    this.alreadyRegistered = true
  }

  /**
   * Register global variables. Avoid this as much as possible. Use them if needed in class constructor only
   */
  void registerAllGlobals() {
    try {
      def systemEnv = System.getenv()

      def jenkinsLocal = false
      if (systemEnv['JENKINS_LOCAL'] != null) {
        jenkinsLocal = true
      }

      IOC.registerGlobal('JENKINS_LOCAL', jenkinsLocal)

      def dockerDataBasePath = '$(pwd)'
      if (systemEnv['JENKINS_DOCKER_DATA_BASEPATH'] != null) {
        dockerDataBasePath = systemEnv['JENKINS_DOCKER_DATA_BASEPATH']
      }

      IOC.registerGlobal('JENKINS_DOCKER_DATA_BASEPATH', dockerDataBasePath)
    } catch (RejectedAccessException) {
      print RejectedAccessException
      IOC.registerGlobal('JENKINS_LOCAL', false)
      IOC.registerGlobal('JENKINS_DOCKER_DATA_BASEPATH', '$(pwd)')
    }
  }

  void registerContext(Script context) {
    IOC.registerSingleton('@context', {
      return context
    })
  }

  void registerDebugger() {
    IOC.registerSingleton(Debugger.class.getName(), {
      return new Debugger(IOC.get('@context'))
    })
  }

  void registerFactories() {
    IOC.register(OptionStringFactory.class.getName(), {
      return new OptionStringFactory(IOC.get('@context'))
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

  void registerHelper() {
    IOC.register(DockerRunnerHelper.class.getName(), {
      return new DockerRunnerHelper(
        IOC.get('@context'),
        IOC.get(Debugger.class.getName()),
        IOC.get(OptionStringFactory.class.getName()),
        IOC.get('JENKINS_DOCKER_DATA_BASEPATH')
      )
    })
  }

  void registerListeners() {
    IOC.registerSingleton(HeaderDisplayerListener.class.getName(), {
      return new HeaderDisplayerListener(IOC.get('@context'), IOC.get(Debugger.class.getName()))
    })
    IOC.registerSingleton(CheckoutListener.class.getName(), {
      return new CheckoutListener(IOC.get('@context'), IOC.get(Debugger.class.getName()))
    })
    IOC.registerSingleton(DockerLoginListener.class.getName(), {
      return new DockerLoginListener(IOC.get('@context'), IOC.get(Debugger.class.getName()), IOC.get(OptionStringFactory.class.getName()))
    })
    IOC.registerSingleton(PreCommitListener.class.getName(), {
      return new PreCommitListener(IOC.get('@context'), IOC.get(Debugger.class.getName()), IOC.get(DockerRunnerHelper.class.getName()))
    })
    IOC.registerSingleton(MoveFilesListener.class.getName(), {
      return new MoveFilesListener(IOC.get('@context'), IOC.get(Debugger.class.getName()))
    })

    IOC.registerSingleton(TerraformInitListener.class.getName(), {
      return new TerraformInitListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformValidateListener.class.getName(), {
      return new TerraformValidateListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformFmtListener.class.getName(), {
      return new TerraformFmtListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformPlanListener.class.getName(), {
      return new TerraformPlanListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformApplyListener.class.getName(), {
      return new TerraformApplyListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformPlanReplayListener.class.getName(), {
      return new TerraformPlanReplayListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformDestroyListener.class.getName(), {
      return new TerraformDestroyListener(IOC.get('@context'))
    })
    IOC.registerSingleton(TerraformArtifactCleanerListener.class.getName(), {
      return new TerraformArtifactCleanerListener(IOC.get('@context'))
    })
  }

  void registerDeprecation() {
    IOC.registerSingleton(DeprecatedMessage.class.getName(), {
      return new DeprecatedMessage(IOC.get('@context'))
    })

    IOC.registerSingleton(DeprecatedFunction.class.getName(), {
      return new DeprecatedFunction(IOC.get('@context'), IOC.get(DeprecatedMessage.class.getName()))
    })
  }
}
