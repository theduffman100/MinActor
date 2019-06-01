package com.l2o.minactor;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SystemTimeActorBrokerTest {
  private SystemTimeActorBroker testee;
  @Mock
  private AttachedActor<String> handler;
  @Mock
  private ActorBrokerProvider provider;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    testee = new SystemTimeActorBroker(provider);
  }

  @Test
  public void testAddEvent() {
    testee.addEvent("event", handler);
    testee.addEvent(ActorBroker.Command.KILL, null);
    testee.run();
    Mockito.verify(handler).handleEvent("event");
  }

  @Test
  public void testAddTimeEventFromNow() {
    testee.addTimeEventFromNow("timeEvent", handler, 200);
    testee.addTimeEventFromNow(ActorBroker.Command.KILL, null, 500);
    long start = System.currentTimeMillis();
    testee.run();
    long duration = System.currentTimeMillis() - start;
    assertEquals(500, duration, 50);
    Mockito.verify(handler).handleEvent("timeEvent");
  }

  @Test
  public void testCancelTimeEvent() {
    testee.addTimeEventFromNow("timeEvent", handler, 200);
    testee.addTimeEventFromNow(ActorBroker.Command.KILL, null, 500);
    // Add an event to kill the time event since events from external threads will
    // not be in the priority queue before start
    testee.addEvent("taskKiller", new EventHandler<String>() {
      @Override
      public void handleEvent(String event) {
        testee.cancelTimeEvent("timeEvent", handler);
      }
    });
    testee.run();
    Mockito.verify(handler, Mockito.never()).handleEvent("timeEvent");
  }

  @Test
  public void testCancelTimeEventInAsyncQueueDoesNotWork() {
    // This may be an unexpected behaviour: Since the event is added out of thread,
    // it will go through
    // the async queue until the thread is running. Removing it at this time will
    // not work.
    testee.addTimeEventFromNow("timeEvent", handler, 200);
    testee.addTimeEventFromNow(ActorBroker.Command.KILL, null, 500);
    Assert.assertFalse(testee.cancelTimeEvent("timeEvent", handler));
    testee.run();
    Mockito.verify(handler).handleEvent("timeEvent");
  }

  @Test
  public void testDispose() {
    testee.dispose(handler);
    Mockito.verify(provider).dispose(handler);
  }

  @Test
  public void testGetCurrentTime() {
    long now = System.currentTimeMillis();
    long result = testee.getCurrentTime();
    Assert.assertTrue(result - now < 50);
  }

  @Test
  public void testAddCommand() {
    ActorBroker.Command.valueOf("KILL");
  }
}
