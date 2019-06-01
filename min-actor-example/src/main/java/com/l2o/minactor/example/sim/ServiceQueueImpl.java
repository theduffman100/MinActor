package com.l2o.minactor.example.sim;

import java.util.ArrayDeque;
import java.util.Deque;

import com.l2o.minactor.EventHandler;
import com.l2o.minactor.call.Call;
import com.l2o.minactor.guice.proxy.GuiceDelegateActor;

public class ServiceQueueImpl extends GuiceDelegateActor<ServiceQueue> {
  private Deque<Job> queue = new ArrayDeque<>();
  private Job served = null;
  private EventHandler<TimeEvent> timeEventHandler = this::onServiceComplete;

  enum TimeEvent {
    COMPLETE
  }

  public boolean use(long time) {
    queue.addLast(new Job(suspendCall(), time));
    lookForWork();
    return true;
  }

  private void lookForWork() {
    if (served == null && !queue.isEmpty()) {
      served = queue.removeFirst();
      addTimeEventFromNow(TimeEvent.COMPLETE, timeEventHandler, served.duration);
    }
  }

  private void onServiceComplete(TimeEvent te) {
    served.call.success(Boolean.TRUE);
    served = null;
    lookForWork();
  }

  private static class Job {
    private Call<Object> call;
    private long duration;

    public Job(Call<Object> call, long duration) {
      this.call = call;
      this.duration = duration;
    }
  }
}
