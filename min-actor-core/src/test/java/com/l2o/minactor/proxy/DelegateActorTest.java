package com.l2o.minactor.proxy;

import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.l2o.minactor.ActorBroker;
import com.l2o.minactor.SystemTimeActorBroker;
import com.l2o.minactor.call.CallResult;

public class DelegateActorTest {
  private DelegateActor<TestInterface> testee;
  private SystemTimeActorBroker testeeBroker;
  private SystemTimeActorBroker callerBroker;
  @Mock
  private Consumer<String> consumer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    testeeBroker = new SystemTimeActorBroker(null);
    callerBroker = new SystemTimeActorBroker(null);
    new Thread(testeeBroker).start();
    new Thread(callerBroker).start();
    testee = new DelegateActor<TestInterface>() {
      @SuppressWarnings("unused")
      public String getMessage(String name) {
        return "Hello " + name;
      }
    };
    testee.attach(testeeBroker);
    ActorBroker.setCurrentBroker(callerBroker);
  }

  @After
  public void tearDown() {
    testeeBroker.addEvent(ActorBroker.Command.KILL, null);
    callerBroker.addEvent(ActorBroker.Command.KILL, null);
    ActorBroker.setCurrentBroker(null);
  }

  @Test
  public void testGetDelegate() throws Exception {
    testee.get().getMessage("Dave").success(consumer);
    Thread.sleep(50);
    Mockito.verify(consumer).accept("Hello Dave");
  }

  interface TestInterface {
    CallResult<String> getMessage(String name);
  }
}
