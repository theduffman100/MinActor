package com.l2o.minactor.call;

/**
 * Task that can be sent to an Actor and with a result that can be sent back to
 * the requester. This is immutable so it can be used as a message.
 *
 * @param <RESULT> The result to be sent back
 */
public interface Call<RESULT> {
  void success(RESULT result);

  void error(Exception result);
}
