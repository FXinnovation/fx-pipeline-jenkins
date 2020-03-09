import com.fxinnovation.di.IOC
import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.listener.standard.*
import com.fxinnovation.observer.EventDispatcher
import com.fxinnovation.listener.standard.CheckoutListener

def call() {
  registerServices()

  def EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())

  // ###
  // General
  // ###
  eventDispatcher.attach(IOC.get(CheckoutListener.class.getName()))

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

  eventDispatcher.dispatch(PipelineEvents.PRE_INIT)
  eventDispatcher.dispatch(PipelineEvents.INIT)
  eventDispatcher.dispatch(PipelineEvents.POST_INIT)
}
