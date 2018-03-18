package com.l2o.minactor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SystemTimeActorBrokerProviderTest {
    private SystemTimeActorBrokerProvider testee;
    @Before
    public void init() {
	testee = new SystemTimeActorBrokerProvider();
    }
    @After
    public void cleanup() {
	testee.terminate();
    }
    @Test
    public void testAssignBrokerEventHandlerAndDispose() throws Exception {
	AttachedActor<String> actor1 = new BaseActor<>();
	AttachedActor<String> actor2 = new BaseActor<>();
	testee.assignBroker(actor1);
	testee.assignBroker(actor2);
	Thread.sleep(50);
	ActorBroker broker = actor1.getBroker();
	Assert.assertSame(broker, actor2.getBroker());
	Assert.assertTrue(broker.isAlive());
	testee.dispose(actor1);
	Thread.sleep(50);
	Assert.assertTrue(broker.isAlive());
	testee.dispose(actor2);
	Thread.sleep(50);
	Assert.assertFalse(broker.isAlive());
    }

    @Test
    public void testObtainBrokerString() throws Exception {
	ActorBroker broker = testee.obtainBroker("test");
	Thread.sleep(50);
	Assert.assertTrue(broker.isAlive());
	broker.addEvent(ActorBroker.Command.KILL, null);
	Thread.sleep(50);
    }

}
