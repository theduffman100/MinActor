package com.l2o.minactor.guice;

import com.google.inject.Inject;
import com.l2o.minactor.ActorBrokerProvider;
import com.l2o.minactor.BaseActor;

/**
 * Guice-injected actor that is automatically injected at creation time.
 *
 * @param <EVENTTYPE> The event type
 */
public class GuiceBaseActor<EVENTTYPE> extends BaseActor<EVENTTYPE> {
    @Inject
    public void initBrokerProvider(ActorBrokerProvider brokerProvider) {
	super.attach(brokerProvider);
    }
}
