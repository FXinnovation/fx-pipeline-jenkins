package com.fxinnovation.observer

import com.fxinnovation.observer.AbstractEventListener

class EventListenerBag {
  private List<AbstractEventListener> eventListeners = []

  String toString() {
    return this.eventListeners
  }

  /**
   * Finds Event Listeners that listens to given eventName, ordered.
   * @param String eventName
   * @return List<AbstractEventListener>
   */
  List<AbstractEventListener> findOrderedListenersForEvent(String eventName) {
    def orderedListeners = [:]
    for (eventListener in this.eventListeners) {
      if (eventListener.supports(eventName)) {
        orderedListeners[this.findNextAvailablePosition(orderedListeners, eventListener)] = eventListener
      }
    }

    return new ArrayList<AbstractEventListener>(orderedListeners.sort().values())
  }

  /**
   * Add an EventListener to the bag.
   * @param AbstractEventListener eventListener
   */
  void addEventListener(AbstractEventListener eventListener) {
    if (this.eventListeners.contains(eventListener)) {
      return
    }

    this.eventListeners.add(eventListener)
  }

  /**
   * Finds the next available position in a Map for the given eventListener.
   * @param Map map
   * @param AbstractEventListener eventListener
   * @return Integer
   */
  private Integer findNextAvailablePosition(Map map, AbstractEventListener eventListener) {
    def eventPosition = eventListener.getOrder()
    while (this.eventPositionExists(map, eventPosition)) {
      eventPosition++
    }

    return eventPosition
  }

  /**
   * Checks whether or not the position given is occupied in the map.
   * @param Map map
   * @param Integer position
   * @return Boolean
   */
  private Boolean eventPositionExists(Map map, Integer position) {
    return map.containsKey(position)
  }
}
