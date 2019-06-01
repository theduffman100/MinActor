package com.l2o.minactor.call;

import java.util.function.Consumer;

/**
 * An object that can be used to attach handlers to inter-actor calls with a
 * return value.
 *
 * @param <RESULT> The result type of the call.
 */
public interface CallResult<RESULT> {
  /**
   * Attach a success handler
   * 
   * @param resultConsumer The success handler as a Consumer
   * @return This CallResult to be used for chaining
   */
  CallResult<RESULT> success(Consumer<RESULT> resultConsumer);

  /**
   * Attach an Exception handler
   * 
   * @param exceptionHandler The Exception handler as a Consumer
   * @return This CallResult to be used for chaining
   */
  CallResult<RESULT> exception(Consumer<Exception> exceptionHandler);

  /**
   * Attach a timeout handler
   * 
   * @param timeMillis     The timeout in milliseconds
   * @param timeOutHandler The timeout handler as a Consumer
   * @return This CallResult to be used for chaining
   */
  CallResult<RESULT> timeout(long timeMillis, Consumer<String> timeOutHandler);
}
