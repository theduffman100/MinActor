package com.l2o.minactor;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.l2o.minactor.call.Call;

public class BaseActorTest {
    private BaseActor<String> testee;
    @Mock
    private ActorBroker broker;
    @Mock
    private AttachedActor<Call<String>> otherActor;
    @Mock
    private Consumer<String> stringConsumer;
    @Mock
    private Consumer<Exception> exceptionConsumer;
    @Mock
    private Consumer<String> timeoutConsumer;
    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
	testee = new BaseActor<>();
	testee.attach(broker);
    }
    @Test
    public void testPostEvent() {
	testee.postEvent("event");
	Mockito.verify(broker).addEvent("event", testee);
    }
    @Test
    public void testAddTimeEventFromNow() {
	testee.addTimeEventFromNow("event", 100L);
	Mockito.verify(broker).addTimeEventFromNow("event", testee, 100L);
    }
    @Test
    public void testDispose() {
	testee.dispose();
	Mockito.verify(broker).dispose(testee);
    }
    @Test
    public void testCancelTimeEvent() {
	testee.cancelTimeEvent("event");
	Mockito.verify(broker).cancelTimeEvent("event", testee);
    }
    @Test
    public void testHandleEvent() {
	new BaseActor<Object>().handleEvent("test");
    }
}
