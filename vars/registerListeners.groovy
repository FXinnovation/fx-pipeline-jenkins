import com.fxinnovation.di.IOC
import com.fxinnovation.listener.standard.*
import com.fxinnovation.observer.EventDispatcher

def call() {
  registerServices()

  def EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())

  // ###
  // General
  // ###
  eventDispatcher.attach(IOC.get(HeaderDisplayerListener.class.getName()))
  eventDispatcher.attach(IOC.get(CheckoutListener.class.getName()))
  eventDispatcher.attach(IOC.get(DockerLoginListener.class.getName()))
  eventDispatcher.attach(IOC.get(PreCommitListener.class.getName()))
  eventDispatcher.attach(IOC.get(MoveFilesListener.class.getName()))

  // ###
  // Terraform
  // ###
  eventDispatcher.attach(IOC.get(TerraformApplyListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformArtifactCleanerListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformDestroyListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformFmtListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformInitListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformPlanListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformPlanReplayListener.class.getName()))
  eventDispatcher.attach(IOC.get(TerraformValidateListener.class.getName()))
}
