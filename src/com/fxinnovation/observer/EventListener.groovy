package com.fxinnovation.observer

abstract class EventListener extends AbstractEventListener {
  /**
   * {@inheritDoc}
   */
  final List<String> getSubscribedEvents() {
    return ['']
  }

  /**
   * {@inheritDoc}
   */
  final Boolean supports(String event) {
    return this.listenTo() == event
  }
}
