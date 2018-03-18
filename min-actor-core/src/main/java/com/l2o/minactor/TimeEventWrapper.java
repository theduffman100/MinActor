package com.l2o.minactor;

/**
 * Internal class (package scope) used to store a time event, its expiry and handler.
 * equals and hashCode are not overridden since they are used to remove event from the
 * priority queue and in this context the expiry is not part of the identity.
 * The parent class implements equals and hashcode based on  the event and handler.
 * This class is Comparable. The sorting is based on the expiry with no tie-breaker.
 * Again this is designed to work with the priority queue. 
 * An event might replace another one with the same timestamp if stored in a sorted map. 
 *
 * @param <T> The type of event
 */
class TimeEventWrapper<T> extends EventWrapper<T> implements Comparable<TimeEventWrapper<?>>{
    private long expiry;
    /**
     * Package-scope constructor to be used for temporary objects to compare with.
     * @param event The event to wrap
     * @Param to The target handler
     */
    TimeEventWrapper(T event, EventHandler<T> to) {
	super(event, to);
    }
    public TimeEventWrapper(T event, EventHandler<T> to, long expiry) {
	this(event, to);
	this.expiry = expiry;
    }
    @Override
    public int compareTo(TimeEventWrapper<?> o) {
	return Long.compare(expiry, o.expiry);
    }
    public long getExpiry() {
	return expiry;
    }
}
