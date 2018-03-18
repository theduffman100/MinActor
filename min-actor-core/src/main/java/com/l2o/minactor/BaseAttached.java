package com.l2o.minactor;

/**
 * A base class that can be attached to a broker.
 */
public class BaseAttached implements Attached {
    private ActorBroker broker;
    
    public void attach(ActorBroker broker) {
	this.broker = broker;
	broker.onAttach(this);
    }
    
    public void attach(ActorBrokerProvider ebp) {
	ebp.assignBroker(this);
    }
    public void attachAlone(ActorBrokerProvider ebp) {
	attach(ebp.obtainBroker("ExclusiveBrokerFor" + getClass().getSimpleName()));
    }
    
    @Override
    public ActorBroker getBroker() {
        return broker;
    }
    protected void dispose() {
	broker.dispose(this);
    }
}
