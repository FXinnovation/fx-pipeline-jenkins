import com.fxinnovation.di.ServiceContainer

def call() {
  ServiceContainer serviceContainer = new ServiceContainer()
  serviceContainer.registerAllClasses(this)
}
