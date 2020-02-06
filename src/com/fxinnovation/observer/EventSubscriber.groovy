package com.fxinnovation.observer

abstract class EventSubscriber extends AbstractEventListener {
  /**
   * {@inheritDoc}
   */
  final String listenTo() {
    return ''
  }

  /**
   * {@inheritDoc}
   */
  final Boolean supports(String event) {
    return this.getSubscribedEvents().contains(event)
  }
}
