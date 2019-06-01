package com.l2o.minactor;

/**
 * An Actor as seen by its broker.
 *
 * @param <EVENTTYPE> The type of events to handle
 */
public interface EventHandler<EVENTTYPE> {
  void handleEvent(EVENTTYPE event);
}
