package com.l2o.minactor;

/**
 * A base class that implements the Actor interface using a broker to manage
 * events asynchronously.
 *
 * @param <EVENTTYPE>
 */
public class BaseActor<EVENTTYPE> extends BaseAttached implements AttachedActor<EVENTTYPE> {
  <TT> void postEvent(TT event, EventHandler<TT> processor) {
    getBroker().addEvent(event, processor);
  }

  @Override
  public void postEvent(EVENTTYPE event) {
    postEvent(event, this);
  }

  @Override
  public void handleEvent(EVENTTYPE event) {
  }

  protected <TT> void addTimeEventFromNow(TT event, EventHandler<TT> processor, long time) {
    getBroker().addTimeEventFromNow(event, processor, time);
  }

  protected void addTimeEventFromNow(EVENTTYPE event, long time) {
    addTimeEventFromNow(event, this, time);
  }

  protected void cancelTimeEvent(EVENTTYPE event) {
    getBroker().cancelTimeEvent(event, this);
  }
}
