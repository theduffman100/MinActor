package com.l2o.minactor.example.sim;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.l2o.minactor.SimTimeRunner;
import com.l2o.minactor.guice.GuiceBaseActor;
import com.l2o.minactor.guice.SimTimeActorModule;

/**
 * An example of a discrete simulation.
 */
public class SimExample {
  static long count = 0;

  public static void main(String[] args) throws Exception {
    Injector injector = Guice.createInjector(new SimTimeActorModule());
    injector.getInstance(CustomerFactory.class).start();
    SimTimeRunner time = injector.getInstance(SimTimeRunner.class);
    time.run(200000);
  }

  public static double getExponentiallyDistributedRandom(double average) {
    return -Math.log(1d - Math.random()) * average;
  }

  private static class CustomerFactory extends GuiceBaseActor<String> {
    @Inject
    private Injector injector;
    @Inject
    ServiceQueue queue;

    private void start() {
      addTimeEventFromNow("CreateBot", (long) getExponentiallyDistributedRandom(200));
    }

    @Override
    public void handleEvent(String event) {
      injector.getInstance(ServiceUser.class).start(queue, (long) getExponentiallyDistributedRandom(400));
      addTimeEventFromNow("CreateBot", 200L);
    }
  }
}
