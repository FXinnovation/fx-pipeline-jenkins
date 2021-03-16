import com.fxinnovation.service.fx.ServiceRegisterer

def call() {
  registerListeners()

  if (ServiceRegisterer.alreadyRegistered) {
    return
  }

  ServiceRegisterer serviceRegisterer = new ServiceRegisterer()
  serviceRegisterer.registerAllClasses()
}
