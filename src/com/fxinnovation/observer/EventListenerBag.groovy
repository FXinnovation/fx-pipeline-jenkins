package com.fxinnovation.observer

import com.fxinnovation.observer.AbstractEventListener

class EventListenerBag {
  private List<AbstractEventListener> eventListeners

  /**
   * Finds Event Listeners that listens to given eventName, ordered.
   * @param String eventName
   * @return Map<AbstractEventListener>
   */
  Map<AbstractEventListener> findOrderedListenersForEvent(String eventName) {
    def orderedListeners = [:]
    for (eventListener in this.eventListeners) {
      if (eventListener.supports(eventName)) {
        orderedListeners[eventListener.getOrder()] = eventListener
      }
    }
    orderedListeners.sort()*.key

    return orderedListeners
  }

  /**
   * Add an EventListener to the bag.
   * @param AbstractEventListener eventListener
   */
  void addEventListener(AbstractEventListener eventListener) {
    this.eventListeners[this.findNextAvailablePosition(eventListener)] = eventListener
  }

  /**
   * Finds the next available position in the bag for the given eventListener.
   * @param AbstractEventListener eventListener
   * @return Integer
   */
  private Integer findNextAvailablePosition(AbstractEventListener eventListener) {
    def eventPosition = eventListener.getOrder()
    while (this.eventPositionExists(eventPosition)) {
      eventPosition++
    }

    return eventPosition
  }

  /**
   * Checks whether or not the position given is occupied.
   * @param Integer position
   * @return Boolean
   */
  private Boolean eventPositionExists(Integer position) {
    return this.eventListeners.containsKey(position)
  }
}
