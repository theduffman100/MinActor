package com.l2o.minactor;

/**
 * An ActorBrokerProvider that holds a single broker and returns it every time.
 */
public class SingletonActorBrokerProvider implements ActorBrokerProvider {
    private ActorBroker broker;
    public SingletonActorBrokerProvider(ActorBroker broker) {
	this.broker = broker;
    }
    @Override
    public void assignBroker(Attached actor) {
	actor.attach(broker);
    }
    @Override
    public ActorBroker obtainBroker(String name) {
	return broker;
    }

    @Override
    public void dispose(Attached handler) {
	// Nothing to do since the global broker is shared
    }
}
