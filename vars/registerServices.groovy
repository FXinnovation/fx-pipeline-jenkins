import com.fxinnovation.service.standard.ServiceRegisterer

def call() {
  if (ServiceRegisterer.alreadyRegistered) {
    return
  }

  ServiceRegisterer serviceRegisterer = new ServiceRegisterer()
  serviceRegisterer.registerAllClasses(this)
}
