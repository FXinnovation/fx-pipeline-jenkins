import com.fxinnovation.di.IOC
import com.fxinnovation.event.PipelineEvents
import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.observer.EventDispatcher
import com.fxinnovation.service.standard.ServiceRegisterer

def call() {
  if (ServiceRegisterer.alreadyRegistered) {
    return
  }

  ServiceRegisterer serviceRegisterer = new ServiceRegisterer()
  serviceRegisterer.registerAllClasses(this)

  def EventDispatcher eventDispatcher = IOC.get(EventDispatcher.class.getName())
  eventDispatcher.dispatch(PipelineEvents.PRE_INIT)
  eventDispatcher.dispatch(PipelineEvents.INIT)
  eventDispatcher.dispatch(PipelineEvents.POST_INIT)
}
