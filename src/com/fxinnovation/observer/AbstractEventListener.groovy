package com.fxinnovation.observer

abstract class AbstractEventListener {
  /**
   * Runs the event.
   * @param EventDataInterface eventData
   * @return EventDataInterface
   */
  abstract EventDataInterface run(EventDataInterface eventData = null)

  /**
   * Returns the event name for which this listener listens to.
   * @return String
   */
  abstract String listenTo()

  /**
   * Returns event names for which this Listener listens to.
   * @return String[]
   */
  abstract List<String> getSubscribedEvents()

  /**
   * Checks whether or not this listener supports the kind of event passed as argument.
   * @param String event
   * @return Boolean
   */
  abstract Boolean supports(String event)

  /**
   * Instructs whether or not this event must be the last one of the serie.
   * @return Boolean
   */
  Boolean stopPropagationAfterRun() {
    return false;
  }

  /**
   * Returns the order in which this event must be triggered.
   * @return Integer
   */
  Integer getOrder() {
    return 1000;
  }
}
