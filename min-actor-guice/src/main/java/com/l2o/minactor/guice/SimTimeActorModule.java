package com.l2o.minactor.guice;

import java.util.concurrent.Future;

import com.google.inject.AbstractModule;
import com.l2o.minactor.ActorBroker;
import com.l2o.minactor.ActorBrokerProvider;
import com.l2o.minactor.SimTimeActorBroker;
import com.l2o.minactor.SimTimeRunner;
import com.l2o.minactor.SingletonActorBrokerProvider;
import com.l2o.minactor.call.BaseCallResult;
import com.l2o.minactor.call.Call;
import com.l2o.minactor.call.CallResult;
import com.l2o.minactor.future.FutureKeeper;

/**
 * This module wires simulated time brokers into Guice. A Singleton SimTimeActorBrokerImpl is
 *  registered as the one and only broker and time provider.
 */
public class SimTimeActorModule extends AbstractModule {
    @Override
    protected void configure() {
	SimTimeActorBroker broker = new SimTimeActorBroker();
	bind(ActorBroker.class).toInstance(broker);
	bind(SimTimeRunner.class).toInstance(broker);
	bind(ActorBrokerProvider.class).toInstance(new SingletonActorBrokerProvider(broker));
	bind(FutureKeeper.class).toInstance(new LimitedFutureKeeper());
    }
    private static class LimitedFutureKeeper implements FutureKeeper {
	@Override
	public <RESULT> CallResult<RESULT> waitFor(Future<RESULT> future) {
	    BaseCallResult<RESULT> result = new BaseCallResult<>(ActorBroker.getCurrentBroker());
	    Call<RESULT> call = result.getTask(future);
	    try {
		call.success(future.get());
	    } catch (Exception ex) {
		call.error(ex);
	    }
	    return result;
	}
    }
}
