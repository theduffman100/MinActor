package com.l2o.minactor.guice.proxy;

import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.ProvidedBy;
import com.l2o.minactor.ActorBroker;
import com.l2o.minactor.SystemTimeActorBroker;
import com.l2o.minactor.call.CallResult;
import com.l2o.minactor.guice.SystemTimeActorModule;

public class GuiceDelegateActorTest {
    private TestInterface testee;
    private SystemTimeActorBroker callerBroker;
    @Mock
    private Consumer<String> consumer;
    
    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	callerBroker = new SystemTimeActorBroker(null);
	new Thread(callerBroker).start();
	testee = Guice.createInjector(new SystemTimeActorModule()).getInstance(TestInterface.class);
	ActorBroker.setCurrentBroker(callerBroker);
    }
    @After
    public void tearDown() {
	callerBroker.addEvent(ActorBroker.Command.KILL, null);
	ActorBroker.setCurrentBroker(null);
    }
    @Test
    public void testGetDelegate() throws Exception {
	testee.getMessage("Dave").success(consumer);
	Thread.sleep(50);
	Mockito.verify(consumer).accept("Hello Dave");
    }
    @ProvidedBy(TestClass.class)
    interface TestInterface {
	CallResult<String> getMessage(String name);
    }
    public static class TestClass extends GuiceDelegateActor<TestInterface> {
	public String getMessage(String name) {
	    return "Hello " + name;
	}

    }
}
