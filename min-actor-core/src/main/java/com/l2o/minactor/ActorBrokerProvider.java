package com.l2o.minactor;

/**
 * Interface for a service that manages and provides ActorBroker's
 */
public interface ActorBrokerProvider {
    /**
     * Gets a broker that may be shared with other handlers (or not depending on the implementation).
     * 
     * @param actor - Not null - The new handler that is requesting a broker - the provider will keep it 
     * 		in a weak hashmap and may eventually shut down the event broker if all handlers are GC'd.
     */
    void assignBroker(Attached actor);
    /**
     * Gets an exclusive broker. An exclusive broker has a dedicated thread and will not suffer performance
     *   impact from other handlers sharing it. Specific implementations may disregard this rule.
     * 
     * @param name The thread name for the broker
     * 
     * @return The event broker
     */
    ActorBroker obtainBroker(String name);
    /**
     * Tell the provider that the handler is no longer in use. 
     * Its broker (or any unused broker) might be stopped.
     * 
     * @param handler
     */
    void dispose(Attached handler);
    default void terminate() {
    }
}
