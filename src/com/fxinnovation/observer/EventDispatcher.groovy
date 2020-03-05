package com.fxinnovation.observer

import com.fxinnovation.observer.AbstractEventListener

class EventDispatcher {
  private EventListenerBag eventListenerBag

  EventDispatcher(EventListenerBag eventListenerBag) {
    this.eventListenerBag = eventListenerBag
  }

  /**
   * Dispatches an event
   * @param String eventName
   * @param EventDataInterface eventData
   */
  EventDataInterface dispatch(String eventName, EventDataInterface eventData = null) {
    for (eventListener in this.eventListenerBag.findOrderedListenersForEvent(eventName)) {
      this.debugger.printDebug("\u001b[36m~~~> Running listener: “\033[0;4m\033[0;1m"+ eventListener.class.getName() +"\u001b[0m\u001b[36m”\u001b[0m")
      def runData = eventListener.run(eventData)
      this.debugger.printDebug("\u001b[36m~~~> End of listener: “\033[0;4m\033[0;1m"+ eventListener.class.getName() +"\u001b[0m\u001b[36m”\u001b[0m")

      if (null != runData) {
         eventData = runData
      }

      if (eventListener.stopPropagationAfterRun()) {
        break
      }
    }

    return eventData
  }

  /**
   * Attaches an EventListener so it can be triggered when the listened events are dispatched.
   * @param AbstractEventListener eventListener
   */
  EventDispatcher attach(AbstractEventListener eventListener) {
    this.eventListenerBag.addEventListener(eventListener)

    return this
  }
}
