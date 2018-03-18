package com.l2o.minactor;

/**
 * Defines a service that manages asynchronous events for Actor objects
 */
public interface ActorBroker {
    static final ThreadLocal<ActorBroker> currentBroker = new ThreadLocal<>();
    <EVENTTYPE> void addEvent(EVENTTYPE event, EventHandler<EVENTTYPE> handler);
    <EVENTTYPE> void addTimeEventFromNow(EVENTTYPE event, EventHandler<EVENTTYPE> handler, long time);
    <EVENTTYPE> boolean cancelTimeEvent(EVENTTYPE event, EventHandler<EVENTTYPE> handler);
    long getCurrentTime();
    void onAttach(Attached actor);
    void dispose(Attached handler);
    boolean isAlive();
    public enum Command {
	KILL
    }
    static ActorBroker getCurrentBroker() {
	return currentBroker.get();
    }
    static ActorBroker setCurrentBroker(ActorBroker broker) {
	ActorBroker retVal = currentBroker.get();
	currentBroker.set(broker);
	return retVal;
    }
    /**
     * Sets the default broker to use for outgoing call that expect a response,
     *   and returns a closeable to restore it to the previous value.
     * @param broker
     * @return
     */
    static SimpleCloseable usingBroker(ActorBroker broker) {
	final ActorBroker old = getCurrentBroker();
	setCurrentBroker(broker);
	return new SimpleCloseable() {
	    @Override
	    public void close() {
		setCurrentBroker(old);
	    }
	};
    }
}
