package com.l2o.minactor;

import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Base actor broker class that implements storage for events and time events
 */
public abstract class BaseActorBroker implements ActorBroker {
    protected BlockingQueue<EventWrapper<?>> queue = new LinkedBlockingQueue<>();
    protected PriorityQueue<TimeEventWrapper<?>> timeEvents = new PriorityQueue<>();
    @Override
    public <EVENTTYPE> void addEvent(EVENTTYPE event, EventHandler<EVENTTYPE> handler) {
	queue.offer(new EventWrapper<EVENTTYPE>(event, handler));
    }
    @Override
    public <EVENTTYPE> void addTimeEventFromNow(EVENTTYPE event, EventHandler<EVENTTYPE> handler, long time) {
	TimeEventWrapper<EVENTTYPE> wrapper = new TimeEventWrapper<EVENTTYPE>(event, handler, getCurrentTime() + time);
	timeEvents.add(wrapper);
    }
    @Override
    public <EVENTTYPE> boolean cancelTimeEvent(EVENTTYPE event, EventHandler<EVENTTYPE> handler) {
	return timeEvents.remove(new TimeEventWrapper<EVENTTYPE>(event, handler));
    }
    @Override
    public void onAttach(Attached actor) {
    }
}
