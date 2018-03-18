package com.l2o.minactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class SingletonActorBrokerProviderTest {
    private SingletonActorBrokerProvider testee;
    @Mock(name="handler1")
    private AttachedActor<String> actor1;
    @Mock(name="handler2")
    private AttachedActor<String> actor2;
    @Mock
    private ActorBroker broker;
    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
	testee = new SingletonActorBrokerProvider(broker);
    }
    @Test
    public void testAssignBrokerEventHandlerAndDispose() throws Exception {
	testee.assignBroker(actor1);
	testee.assignBroker(actor2);
	Thread.sleep(50);
	ArgumentCaptor<ActorBroker> cap1 = ArgumentCaptor.forClass(ActorBroker.class);
	Mockito.verify(actor1, Mockito.times(1)).attach(cap1.capture());
	ArgumentCaptor<ActorBroker> cap2 = ArgumentCaptor.forClass(ActorBroker.class);
	Mockito.verify(actor2, Mockito.times(1)).attach(cap2.capture());
	Assert.assertEquals(1, cap1.getAllValues().size());
	Assert.assertEquals(1, cap2.getAllValues().size());
	Assert.assertSame(broker, cap1.getValue());
	Assert.assertSame(broker, cap2.getValue());
	Assert.assertSame(broker, testee.obtainBroker("test"));
    }
}
