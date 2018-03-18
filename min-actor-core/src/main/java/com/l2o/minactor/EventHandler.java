package com.l2o.minactor;

/**
 * An Actor as seen by its broker.
 *
 * @param <T> The type of events
 */
public interface EventHandler<EVENTTYPE> {
    void handleEvent(EVENTTYPE event);
}
