package com.l2o.minactor;

/**
 * An interface for an Actor that can be attached to a broker.
 * This is used internally by the broker to wire itself.
 * This is also used to create delegate actors that can be connected to its master's broker.
 */
public interface Attached {
    ActorBroker getBroker();
    void attach(ActorBroker broker);
}
