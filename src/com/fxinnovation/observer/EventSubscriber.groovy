package com.fxinnovation.observer

abstract class EventSubscriber extends AbstractListenerInterface {
  /**
   * Returns the event name for which this listener listens to.
   * @return String
   */
  final String listenTo() {
    return ''
  }

  /**
   * Checks whether or not this listener supports the kind of event passed as argument.
   * @param String event
   * @return Boolean
   */
  final Boolean supports(String event) {
    return this.getSubscribedEvents().containsValue(event)
  }
}
