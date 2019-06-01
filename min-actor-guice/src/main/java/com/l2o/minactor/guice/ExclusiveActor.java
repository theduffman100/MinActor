package com.l2o.minactor.guice;

import com.google.inject.Inject;
import com.l2o.minactor.ActorBrokerProvider;

public class ExclusiveActor<EVENTTYPE> extends GuiceBaseActor<EVENTTYPE> {
  @Inject
  public void initBrokerProvider(ActorBrokerProvider brokerProvider) {
    super.attachAlone(brokerProvider);
  }
}
