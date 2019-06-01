package com.l2o.minactor.guice;

import com.google.inject.Inject;
import com.l2o.minactor.ActorBrokerProvider;
import com.l2o.minactor.BaseAttached;

/**
 * Guice-injected broker-attached object that is automatically injected at
 * creation time.
 */
public class GuiceBaseAttached extends BaseAttached {
  @Inject
  public void initBrokerProvider(ActorBrokerProvider brokerProvider) {
    super.attach(brokerProvider);
  }
}
