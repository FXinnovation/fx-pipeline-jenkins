import com.fxinnovation.service.standard.ServiceRegisterer

def call() {
  ServiceRegisterer serviceRegisterer = new ServiceRegisterer()
  serviceRegisterer.registerAllClasses(this)
}
