import com.fxinnovation.service.fx.ServiceRegisterer

def call() {
  ServiceRegisterer serviceRegisterer = new ServiceRegisterer()
  serviceRegisterer.registerAllClasses(this)
}
