package com.l2o.minactor.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.l2o.minactor.ActorBrokerProvider;
import com.l2o.minactor.SystemTimeActorBrokerProvider;

@Singleton
public class GuiceSystemTimeActorBrokerProvider extends SystemTimeActorBrokerProvider implements ActorBrokerProvider {
  @Inject(optional = true)
  @Named("minactor.handlers.per.thread")
  private int maxHandlersPerThread = 20;

  @Override
  public int getMaxHandlersPerThread() {
    return maxHandlersPerThread;
  }
}
