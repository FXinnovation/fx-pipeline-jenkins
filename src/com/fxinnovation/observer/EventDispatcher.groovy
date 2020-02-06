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
  void dispatch(String eventName, EventDataInterface eventData = null) {
    for (eventListener in this.eventListenerBag.findOrderedListenersForEvent(eventName)) {
      eventListener.run(eventData)

      if (eventListener.stopPropagationAfterRun()) {
        break
      }
    }
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
