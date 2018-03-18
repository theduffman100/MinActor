package com.l2o.minactor.guice.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.l2o.minactor.ActorBrokerProvider;
import com.l2o.minactor.proxy.DelegateActor;

/**
 * This class adds the Provider interface so that interfaces can use the @ProvidedBy annotation
 *   with a subclass of this.
 *
 * @param <INTERFACE>
 */
public class GuiceDelegateActor<INTERFACE> extends DelegateActor<INTERFACE> implements Provider<INTERFACE> {
    @Inject
    public void initBrokerProvider(ActorBrokerProvider brokerProvider) {
	super.attach(brokerProvider);
    }
}
