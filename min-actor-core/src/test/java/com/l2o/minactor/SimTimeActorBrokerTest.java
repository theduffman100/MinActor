package com.l2o.minactor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SimTimeActorBrokerTest {
    private SimTimeActorBroker testee;
    @Mock
    private AttachedActor<String> handler;
    @Mock
    private ActorBrokerProvider provider;
    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
	testee = new SimTimeActorBroker();
    }
    @Test
    public void testAddEvent() {
	testee.addEvent("event", handler);
	testee.run(1000L);
	Mockito.verify(handler).handleEvent("event");
    }

    @Test
    public void testAddTimeEventFromNow() {
	testee.addTimeEventFromNow("timeEvent", handler, 200);
	long start = System.currentTimeMillis();
	testee.run(1000L);
	long duration = System.currentTimeMillis() - start;
	assertEquals(0, duration, 50);
	assertEquals(200L, testee.getCurrentTime());
	Mockito.verify(handler).handleEvent("timeEvent");
    }

    @Test
    public void testCancelTimeEvent() {
	testee.addTimeEventFromNow("timeEvent", handler, 200);
	testee.addTimeEventFromNow(ActorBroker.Command.KILL, null, 500);
	// Add an event to kill the time event since events from external threads will not be in the priority queue before start
	testee.addEvent("taskKiller", new EventHandler<String>() {
	    @Override
	    public void handleEvent(String event) {
		testee.cancelTimeEvent("timeEvent", handler);
	    }
	});
	testee.run(1000L);
	Mockito.verify(handler, Mockito.never()).handleEvent("timeEvent");
    }

    @Test
    public void testDispose() {
	testee.dispose(handler);
	Mockito.verify(provider, Mockito.never()).dispose(handler);
    }
}
