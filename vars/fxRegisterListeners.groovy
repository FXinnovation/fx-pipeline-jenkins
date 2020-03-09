import com.fxinnovation.di.IOC
import com.fxinnovation.listener.fx.*
import com.fxinnovation.observer.EventDispatcher

def call() {
  fxRegisterServices()
  registerListeners()

  def EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())

  // ###
  // Terraform
  // ###
  eventDispatcher.attach(IOC.get(TerraformInitListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformRepositoryNameStandardListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformFileStandardListener.class.getName()))
}
