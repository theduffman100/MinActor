package com.l2o.minactor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * System time implementation of the ActorBroker interface. This implementation
 * will handle timers efficiently by parking the thread while waiting for either
 * the next time event or an asynchronous event.
 */
public class SystemTimeActorBroker extends BaseActorBroker implements ActorBroker, Runnable {
  private ActorBrokerProvider provider;
  private static final Logger log = Logger.getLogger(SystemTimeActorBroker.class.getSimpleName());
  private Thread thread;

  public SystemTimeActorBroker(ActorBrokerProvider provider) {
    this.provider = provider;
  }

  @Override
  public void run() {
    this.thread = Thread.currentThread();
    ActorBroker.setCurrentBroker(this);
    while (true) {
      try {
        EventWrapper<?> wrapper;
        // Get the next time event to occur.
        TimeEventWrapper<?> nextTimeEvent = timeEvents.peek();
        if (nextTimeEvent != null) {
          // If the event is expired, execute it
          long remaining = nextTimeEvent.getExpiry() - System.currentTimeMillis();
          if (remaining <= 0) {
            wrapper = timeEvents.remove();
          } else {
            // Otherwise park the thread until there is an asynchronous event or the time
            // event is expired
            wrapper = queue.poll(remaining, TimeUnit.MILLISECONDS);
          }
        } else {
          // Park the thread until there is an asynchronous event
          wrapper = queue.take();
        }
        if (wrapper == null) {
          continue;
        }
        // Handle the asynchronous event
        Object event = wrapper.getEvent();
        if (event == Command.KILL) {
          break;
        } else if (event instanceof TimeEventWrapper) {
          timeEvents.add((TimeEventWrapper<?>) wrapper.getEvent());
          continue;
        }
        triggerEvent(wrapper);
      } catch (InterruptedException iex) {
        return;
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    thread = null;
  }

  private <EVENTTYPE> void triggerEvent(EventWrapper<EVENTTYPE> wrapper) {
    try {
      wrapper.getHandler().handleEvent(wrapper.getEvent());
    } catch (Throwable th) {
      log.log(Level.WARNING, "An exception was thrown by an event handler", th);
      th.printStackTrace();
    }
  }

  @Override
  public <EVENTTYPE> void addTimeEventFromNow(EVENTTYPE event, EventHandler<EVENTTYPE> handler, long time) {
    TimeEventWrapper<EVENTTYPE> wrapper = new TimeEventWrapper<EVENTTYPE>(event, handler,
        System.currentTimeMillis() + time);
    if (Thread.currentThread() == thread) {
      // Covers most of the cases
      timeEvents.add(wrapper);
    } else {
      // We're not in the actor's thread and the time event priority queue is not
      // thread-safe.
      // Post it as an asynchronous event and the run() method will safely add it to
      // the priority queue.
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "A time event was triggered outside of the actor thread. Adding to async queue.");
      }
      queue.offer(new EventWrapper<>(wrapper, null));
    }
  }

  @Override
  public void dispose(Attached handler) {
    provider.dispose(handler);
  }

  @Override
  public long getCurrentTime() {
    return System.currentTimeMillis();
  }

  @Override
  public boolean isAlive() {
    return thread != null;
  }
}
