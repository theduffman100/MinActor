package com.l2o.minactor.future;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import com.l2o.minactor.ActorBrokerProvider;
import com.l2o.minactor.EventHandler;
import com.l2o.minactor.call.Call;
import com.l2o.minactor.proxy.DelegateActor;

/**
 * Actor proxy provider for FutureKeeper
 */
public class FutureKeeperProvider extends DelegateActor<FutureKeeper> {
  private List<WaitedFuture> work = new LinkedList<>();
  private long refreshInterval = 20L;
  private EventHandler<TimeEvent> timeEventHandler = this::handleTimeEvent;

  enum TimeEvent {
    REFRESH
  }

  public FutureKeeperProvider(ActorBrokerProvider factory) {
    super.attachAlone(factory);
    scheduleRefresh();
  }

  private void scheduleRefresh() {
    addTimeEventFromNow(TimeEvent.REFRESH, timeEventHandler, refreshInterval);
  }

  private void handleTimeEvent(TimeEvent te) {
    try {
      for (Iterator<WaitedFuture> it = work.iterator(); it.hasNext();) {
        WaitedFuture task = it.next();
        if (task.isDone()) {
          it.remove();
          task.done();
        }
      }
    } finally {
      scheduleRefresh();
    }
  }

  public void setRefreshInterval(long refreshInterval) {
    this.refreshInterval = refreshInterval;
  }

  public <RESULT> void waitFor(Future<RESULT> future) {
    WaitedFuture task = new WaitedFuture(suspendCall(), future);
    if (task.isDone()) {
      task.done();
      return;
    }
    work.add(task);
  }

  private static class WaitedFuture {
    private Call<Object> call;
    private Future<?> future;

    private WaitedFuture(Call<Object> call, Future<?> future) {
      this.call = call;
      this.future = future;
    }

    private void done() {
      try {
        call.success(future.get());
      } catch (Exception ex) {
        call.error(ex);
      }
    }

    private boolean isDone() {
      return future.isDone();
    }
  }
}
