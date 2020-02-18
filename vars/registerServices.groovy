import com.fxinnovation.di.ServiceRegisterer

def call() {
  ServiceRegisterer serviceRegisterer = new ServiceRegisterer()
  serviceRegisterer.registerAllClasses(this)
}
