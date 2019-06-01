package com.l2o.minactor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simulated time implementation of the ActorBroker interface. This will consume
 * asynchronous events in an eager fashion, and advance the current simulated
 * time as it consumes time events.
 */
public class SimTimeActorBroker extends BaseActorBroker implements SimTimeRunner {
  private long currentTime = 0L;
  private static final Logger log = Logger.getLogger(SimTimeActorBroker.class.getSimpleName());

  @Override
  public void run(long endOfTime) {
    ActorBroker.setCurrentBroker(this);
    while (true) {
      try {
        EventWrapper<?> wrapper = queue.poll();
        if (wrapper == null) {
          TimeEventWrapper<?> nextTimeEvent = timeEvents.peek();
          if (nextTimeEvent == null || nextTimeEvent.getExpiry() > endOfTime) {
            break;
          }
          timeEvents.remove();
          currentTime = nextTimeEvent.getExpiry();
          wrapper = nextTimeEvent;
        }
        // Handle the asynchronous event
        Object event = wrapper.getEvent();
        if (event == Command.KILL) {
          break;
        }
        triggerEvent(wrapper);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private <EVENTTYPE> void triggerEvent(EventWrapper<EVENTTYPE> wrapper) {
    try {
      wrapper.getHandler().handleEvent(wrapper.getEvent());
    } catch (Throwable th) {
      log.log(Level.WARNING, "An exception was thrown by an event handler", th);
      th.printStackTrace();
    }
  }

  public long getCurrentTime() {
    return currentTime;
  }

  @Override
  public void dispose(Attached handler) {
  }

  @Override
  public boolean isAlive() {
    return true;
  }
}
