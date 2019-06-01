package com.l2o.minactor.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.l2o.minactor.ActorBrokerProvider;
import com.l2o.minactor.future.FutureKeeper;
import com.l2o.minactor.future.FutureKeeperProvider;

/**
 * This module wires simulated time brokers into Guice. A Singleton
 * SimTimeActorBrokerImpl is registered as the one and only broker and time
 * provider.
 */
public class SystemTimeActorModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ActorBrokerProvider.class).toInstance(new GuiceSystemTimeActorBrokerProvider());
  }

  @Provides
  @Singleton
  public FutureKeeper createFutureKeeper(ActorBrokerProvider actorBrokerProvider) {
    return new FutureKeeperProvider(actorBrokerProvider).get();
  }
}
