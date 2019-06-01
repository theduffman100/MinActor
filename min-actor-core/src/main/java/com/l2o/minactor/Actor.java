package com.l2o.minactor;

/**
 * Defines an object that is able to receive asynchronous event.
 *
 * @param <EVENTTYPE> The type of event
 */
public interface Actor<EVENTTYPE> {
  void postEvent(EVENTTYPE event);
}
