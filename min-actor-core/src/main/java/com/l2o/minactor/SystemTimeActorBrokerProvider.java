package com.l2o.minactor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A pooled, system time implementation of ActorBrokerProvider.
 * ActorBrokers may share a common thread depending on how they obtain the broker.
 * They are thus expected to behave properly and not block while handling an event.
 */
public class SystemTimeActorBrokerProvider extends BaseActor<Runnable> implements ActorBrokerProvider {
    private int idx = 0;
    private List<BrokerWrapper> brokers = new ArrayList<>();
    
    public SystemTimeActorBrokerProvider() {
	attach(obtainBroker("ActorBrokerProvider"));
    }
    
    /**
     * Obtain a pooled broker. Brokers will handle up to MaxHandlersPerThread handlers.
     * When all existing brokers reach that limit, new brokers will be allocated.
     * The actor will first be attributed a temporary threadless broker, then the actual pooled broker
     *   will be attributed in an asynchronous event and all events received by the temporary
     *   worker will be transferred to the permanent broker.
     *
     * @param handler - Not null - The handler that is requesting a broker - the provider will keep it 
     * 		in a weak hashmap and may eventually shut down the event broker if all handlers are GC'd.
     * 
     * @return The event broker
     */
    @Override
    public void assignBroker(Attached actor) {
	TemporaryBroker tempBroker = new TemporaryBroker();
	actor.attach(tempBroker);
	postEvent(() -> tempBroker.transfer(obtainBroker(actor)));
    }
    @Override
    public void handleEvent(Runnable event) {
	event.run();
    }
    private BaseActorBroker obtainBroker(Attached handler) {
	BrokerWrapper minWrapper = null;
	int minCount = Integer.MAX_VALUE;
	int maxHandlersPerThread = getMaxHandlersPerThread();
	for (Iterator<BrokerWrapper> it = brokers.iterator(); it.hasNext();) {
	    BrokerWrapper wrapper = it.next();
	    int count = wrapper.getHandlerCount();
	    if (count < minCount) {
		minWrapper = wrapper;
		minCount = count;
	    } else if (count == 0) {
		// This excludes the first empty
		wrapper.broker.addEvent(ActorBroker.Command.KILL, null);
		it.remove();
	    }
	}
	if (minWrapper == null || minCount >= maxHandlersPerThread) {
	    ++idx;
	    BaseActorBroker broker = createBroker("EventBroker_" + idx);
	    minWrapper = new BrokerWrapper();
	    minWrapper.broker = broker;
	    brokers.add(minWrapper);
	}
	minWrapper.register(handler);
	return minWrapper.broker;
    }
    public void dispose(Attached handler) {
	postEvent(() -> doDispose(handler));
    }
    public void doDispose(Attached handler) {
	for (Iterator<BrokerWrapper> it = brokers.iterator(); it.hasNext();) {
	    BrokerWrapper wrapper = it.next();
	    wrapper.unRegister(handler);
	    if (wrapper.getHandlerCount() == 0) {
		wrapper.broker.addEvent(ActorBroker.Command.KILL, null);
		it.remove();
	    }
	}
    }
    public void terminate() {
	getBroker().dispose(this);
    }

    /**
     * Obtain an exclusive broker. This broker will not be available in the pool.
     */
    @Override
    public ActorBroker obtainBroker(String name) {
	return createBroker(name);
    }
    private SystemTimeActorBroker createBroker(String name) {
	SystemTimeActorBroker broker = new SystemTimeActorBroker(this);
	Thread thread = new Thread(broker);
	thread.setName(name);
	thread.setDaemon(true);
	thread.start();
	return broker;
    }
    private static class BrokerWrapper {
	private WeakHashMap<Attached, Boolean> refMap = new WeakHashMap<>();
	private BaseActorBroker broker;
	private void register(Attached handler) {
	    refMap.put(handler, Boolean.TRUE);
	}
	private void unRegister(Attached handler) {
	    refMap.remove(handler);
	}
    	private int getHandlerCount() {
    	    return refMap.size();
    	}
    }
    public int getMaxHandlersPerThread() {
	return 20;
    }
    private static class TemporaryBroker implements ActorBroker {
	private List<Attached> actors = new ArrayList<>();
	private BlockingQueue<EventWrapper<?>> queue = new LinkedBlockingQueue<>();
	private AtomicReference<ActorBroker> transferredBroker = new AtomicReference<>(null);
	@Override
	public void onAttach(Attached actor) {
	    actors.add(actor);
	}
	private void transfer(BaseActorBroker broker) {
	    for (Attached actor : actors) {
		actor.attach(broker);
	    }
	    drain(broker);
	    transferredBroker.set(broker);
	    drain(broker);
	}
	private void drain(BaseActorBroker broker) {
	    while (true) {
		EventWrapper<?> wrapper = queue.poll();
		if (wrapper == null) {
		    break;
		}
		broker.queue.offer(wrapper);
	    }
	}
	@Override
	public long getCurrentTime() {
	    return System.currentTimeMillis();
	}
	@Override
	public void dispose(Attached handler) {
	}
	
	@Override
	public <EVENTTYPE> void addTimeEventFromNow(EVENTTYPE event, EventHandler<EVENTTYPE> handler, long time) {
	    addEvent(new TimeEventWrapper<EVENTTYPE>(event, handler, System.currentTimeMillis() + time), null);
	}

	@Override
	public boolean isAlive() {
	    return false;
	}
	@Override
	public <EVENTTYPE> void addEvent(EVENTTYPE event, EventHandler<EVENTTYPE> handler) {
	    ActorBroker transferred = transferredBroker.get();
	    if (transferred != null) {
		transferred.addEvent(event, handler);
	    }
	    else {
		queue.add(new EventWrapper<>(event, handler));
	    }
	}
	@Override
	public <EVENTTYPE> boolean cancelTimeEvent(EVENTTYPE event, EventHandler<EVENTTYPE> handler) {
	    ActorBroker transferred = transferredBroker.get();
	    if (transferred != null) {
		return transferred.cancelTimeEvent(event, handler);
	    }
	    return false;
	}
	
    }
}
