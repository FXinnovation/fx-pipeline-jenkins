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
      def runData = eventListener.run(eventData)

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
