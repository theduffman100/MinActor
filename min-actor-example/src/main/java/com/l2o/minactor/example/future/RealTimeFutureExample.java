package com.l2o.minactor.example.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.l2o.minactor.ActorBroker;
import com.l2o.minactor.SimpleCloseable;
import com.l2o.minactor.future.FutureKeeper;
import com.l2o.minactor.guice.GuiceBaseActor;
import com.l2o.minactor.guice.SystemTimeActorModule;

/**
 * This shows how to wait for futures with callbacks and demonstrates the normal
 * and timeout cases.
 */
public class RealTimeFutureExample extends GuiceBaseActor<String> {
    static long baseTime = System.currentTimeMillis();
    @Inject
    private FutureKeeper futureKeeper;

    static String getTimeStr() {
	return " - Time: " + (System.currentTimeMillis() - baseTime);
    }

    public static void main(String[] args) throws Exception {
	Injector injector = Guice.createInjector(new SystemTimeActorModule());
	injector.getInstance(RealTimeFutureExample.class);
	Thread.sleep(5000);
	System.out.println("Goodbye!" + getTimeStr());
	executor.shutdown();
    }

    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    public void handleEvent(String event) {
	print(event);
    }

    @Inject
    public void init() {
	try (SimpleCloseable active = ActorBroker.usingBroker(getBroker())) {
	    baseTime = System.currentTimeMillis();
	    Callable<String> delayedString = new Callable<String>() {
		@Override
		public String call() throws Exception {
		    Thread.sleep(2000);
		    return "Hello, world!";
		}
	    };
	    Callable<String> delayedString2 = new Callable<String>() {
		@Override
		public String call() throws Exception {
		    Thread.sleep(4000); // Should timeout
		    return "You shouldn't see this";
		}
	    };
	    addTimeEventFromNow("Hi", 500);
	    futureKeeper.waitFor(executor.submit(delayedString)).success(RealTimeFutureExample::print).timeout(3000L,
		    RealTimeFutureExample::print);
	    futureKeeper.waitFor(executor.submit(delayedString2)).success(RealTimeFutureExample::print).timeout(3000L,
		    RealTimeFutureExample::print);
	}
    }

    static void print(Object obj) {
	System.out.println("Got " + obj + getTimeStr());
    }
}
