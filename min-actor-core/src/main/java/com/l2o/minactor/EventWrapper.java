package com.l2o.minactor;

import java.util.Objects;

/**
 * Internal class (package scope) used to store an event and its handler.
 * The identity is based on event (using equals) and handler address. 
 *
 * @param <EVENTTYPE> The type of event
 */
class EventWrapper<EVENTTYPE> {
    private EVENTTYPE event;
    private EventHandler<EVENTTYPE> handler;
    public EventWrapper(EVENTTYPE event, EventHandler<EVENTTYPE> handler) {
	this.event = event;
	this.handler= handler;
    }
    public EVENTTYPE getEvent() {
	return event;
    }
    public EventHandler<EVENTTYPE> getHandler() {
	return handler;
    }
    @Override
    public boolean equals(Object obj) {
	// Compare inner event and destination. This is used for removal.
	if (!(obj instanceof EventWrapper)) {
	    return false;
	}
	EventWrapper<?> other = (EventWrapper<?>)obj;
	return Objects.equals(event, other.event) && handler == other.handler;
    }
    @Override
    public int hashCode() {
        return System.identityHashCode(handler) + (event == null ? 0 : event.hashCode());
    }
}
