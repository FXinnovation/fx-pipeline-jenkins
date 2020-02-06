package com.fxinnovation.observer

abstract class EventListener extends AbstractEventListener {
  final String[] getSubscribedEvents() {
    return ['']
  }

  /**
   * Checks whether or not this listener supports the kind of event passed as argument.
   * @param String event
   * @return Boolean
   */
  final Boolean supports(String event) {
    return this.listenTo() == event
  }
}
