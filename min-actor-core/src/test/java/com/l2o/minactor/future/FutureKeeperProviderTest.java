package com.l2o.minactor.future;

import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.l2o.minactor.ActorBroker;
import com.l2o.minactor.SimTimeActorBroker;
import com.l2o.minactor.SingletonActorBrokerProvider;

public class FutureKeeperProviderTest {
    private FutureKeeperProvider testee;
    private FutureKeeper proxy;
    private SimTimeActorBroker broker;
    @Mock
    private Future<String> future;
    @Mock
    private Consumer<String> successHandler;
    @Mock
    private Consumer<Exception> errorHandler;
    
    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
	broker = new SimTimeActorBroker();
	testee = new FutureKeeperProvider(new SingletonActorBrokerProvider(broker));
	proxy = testee.get();
	testee.setRefreshInterval(20L);
	ActorBroker.setCurrentBroker(broker);
    }
    @Test
    public void testWaitFor() throws Exception {
	proxy.waitFor(future).success(successHandler).exception(errorHandler);
	broker.run(30);
	Mockito.verify(successHandler, Mockito.never()).accept(Mockito.anyString());
	Mockito.verify(errorHandler, Mockito.never()).accept(Mockito.anyObject());
	Mockito.when(future.isDone()).thenReturn(Boolean.TRUE);
	Mockito.when(future.get()).thenReturn("23");
	broker.run(50);
	Mockito.verify(successHandler, Mockito.times(1)).accept("23");
    }
    @Test
    public void testWaitForException() throws Exception {
	proxy.waitFor(future).success(successHandler).exception(errorHandler);
	broker.run(30);
	Mockito.verify(successHandler, Mockito.never()).accept(Mockito.anyString());
	Mockito.verify(errorHandler, Mockito.never()).accept(Mockito.anyObject());
	Mockito.when(future.isDone()).thenReturn(Boolean.TRUE);
	IllegalStateException exception = new IllegalStateException("Exception for test");
	Mockito.when(future.get()).thenThrow(exception);
	broker.run(50);
	Mockito.verify(successHandler, Mockito.never()).accept(Mockito.any());
	Mockito.verify(errorHandler, Mockito.times(1)).accept(exception);
    }
    @Test
    public void testHandleEventReady() throws Exception {
	Mockito.when(future.isDone()).thenReturn(Boolean.TRUE);
	Mockito.when(future.get()).thenReturn("23");
	proxy.waitFor(future).success(successHandler).exception(errorHandler);
	broker.run(30);
	Mockito.verify(successHandler, Mockito.times(1)).accept("23");
    }
    @Test
    public void testHandleEventReadyException() throws Exception {
	Mockito.when(future.isDone()).thenReturn(Boolean.TRUE);
	IllegalStateException exception = new IllegalStateException("Exception for test");
	Mockito.when(future.get()).thenThrow(exception);
	proxy.waitFor(future).success(successHandler).exception(errorHandler);
	broker.run(30);
	Mockito.verify(successHandler, Mockito.never()).accept(Mockito.any());
	Mockito.verify(errorHandler, Mockito.times(1)).accept(exception);
    }
    @Test
    public void testHandleEventNotReadyThenTimeEventReady() throws Exception {
	Mockito.when(future.isDone()).thenReturn(Boolean.FALSE);
	proxy.waitFor(future).success(successHandler).exception(errorHandler);
	broker.run(30);
	Mockito.verify(future, Mockito.times(2)).isDone();
	Mockito.verify(successHandler, Mockito.never()).accept(Mockito.anyString());
	Mockito.verify(errorHandler, Mockito.never()).accept(Mockito.anyObject());
	Mockito.when(future.isDone()).thenReturn(Boolean.FALSE);
	broker.run(50);
	Mockito.verify(future, Mockito.times(3)).isDone();
	Mockito.verify(successHandler, Mockito.never()).accept(Mockito.anyString());
	Mockito.verify(errorHandler, Mockito.never()).accept(Mockito.anyObject());
	Mockito.when(future.isDone()).thenReturn(Boolean.TRUE);
	Mockito.when(future.get()).thenReturn("23");
	broker.run(70);
	Mockito.verify(future, Mockito.times(4)).isDone();
	Mockito.verify(successHandler, Mockito.times(1)).accept("23");
    }
}
