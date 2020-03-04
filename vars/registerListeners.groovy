import com.fxinnovation.di.IOC
import com.fxinnovation.listener.standard.*
import com.fxinnovation.observer.EventDispatcher

def call() {
  registerServices()

  def EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())
  def TerraformInitListener terraformInitListener = IOC.get(TerraformInitListener.class.getName())

  eventDispatcher.attach(terraformInitListener)
}
