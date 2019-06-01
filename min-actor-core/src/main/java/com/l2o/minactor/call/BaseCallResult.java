package com.l2o.minactor.call;

import java.util.function.Consumer;

import com.l2o.minactor.ActorBroker;
import com.l2o.minactor.BaseActor;

/**
 * An object that can be used to attach handlers to inter-actor calls with a
 * return value.
 *
 * @param <RESULT> The result type of the call.
 */
public class BaseCallResult<RESULT> implements CallResult<RESULT> {
  private static final String TIMEOUT = "Task timed out";
  private final ActorAdapter<RESULT> successHandler;
  private final ActorAdapter<Exception> exceptionHandler;
  private final ActorAdapter<String> timeOutHandler;

  /**
   * Package-scope constructor. This object is created by the BaseActor and it
   * shares its broker to ensure thread safety.
   * 
   * @param broker The broker to use
   */
  public BaseCallResult(ActorBroker broker) {
    successHandler = new ActorAdapter<>(broker);
    exceptionHandler = new ActorAdapter<>(broker);
    timeOutHandler = new ActorAdapter<>(broker);
  }

  public <PARAM> CallWithParam<PARAM, RESULT> getTask(PARAM param) {
    return new CallImpl<>(param, successHandler, exceptionHandler);
  }

  /**
   * Attach a success handler
   * 
   * @param resultConsumer The success handler as a Consumer
   * @return This CallResult to be used for chaining
   */
  public BaseCallResult<RESULT> success(Consumer<RESULT> resultConsumer) {
    this.successHandler.setConsumer(resultConsumer);
    return this;
  }

  /**
   * Attach an Exception handler
   * 
   * @param exceptionHandler The Exception handler as a Consumer
   * @return This CallResult to be used for chaining
   */
  public BaseCallResult<RESULT> exception(Consumer<Exception> exceptionHandler) {
    this.exceptionHandler.setConsumer(exceptionHandler);
    return this;
  }

  /**
   * Attach a timeout handler
   * 
   * @param timeMillis     The timeout in milliseconds
   * @param timeOutHandler The timeout handler as a Consumer
   * @return This CallResult to be used for chaining
   */
  public BaseCallResult<RESULT> timeout(long timeMillis, Consumer<String> timeOutHandler) {
    if (exceptionHandler.value != null || successHandler.value != null) {
      return this;
    }
    this.timeOutHandler.setConsumer(timeOutHandler);
    this.timeOutHandler.addTimeEventFromNow(TIMEOUT, timeMillis);
    return this;
  }

  private boolean isTimedOut() {
    return timeOutHandler.value != null;
  }

  private void cancelTimer() {
    timeOutHandler.cancelTimeEvent(TIMEOUT);
  }

  private class ActorAdapter<T> extends BaseActor<T> {
    private T value = null;
    private Consumer<T> consumer;

    private ActorAdapter(ActorBroker broker) {
      attach(broker);
    }

    @Override
    public void handleEvent(T value) {
      if (isTimedOut()) {
        return;
      }
      this.value = value;
      if (!isTimedOut()) {
        // This indicates that we're not handling a time out
        cancelTimer();
      }
      if (consumer != null) {
        consumer.accept(value);
      }
    }

    private void setConsumer(Consumer<T> consumer) {
      this.consumer = consumer;
      if (value != null) {
        consumer.accept(value);
      }
    }

    @Override
    protected void cancelTimeEvent(T event) {
      super.cancelTimeEvent(event);
    }

    @Override
    protected void addTimeEventFromNow(T event, long time) {
      super.addTimeEventFromNow(event, time);
    }
  }
}
