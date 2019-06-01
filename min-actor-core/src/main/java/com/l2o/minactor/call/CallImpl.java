package com.l2o.minactor.call;

import com.l2o.minactor.Actor;

/**
 * Call implementation that has a result processor and an exception processor as
 * actors.
 *
 * @param <PARAM> The message to be sent
 * @param <RESULT> The result to be sent back
 */
public class CallImpl<PARAM, RESULT> implements CallWithParam<PARAM, RESULT> {
  private PARAM param;
  private Actor<RESULT> resultProcessor;
  private Actor<Exception> exceptionProcessor;

  public CallImpl(PARAM param, Actor<RESULT> processor) {
    this(param, processor, null);
  }

  public CallImpl(PARAM param, Actor<RESULT> resultProcessor, Actor<Exception> exceptionProcessor) {
    this.param = param;
    this.resultProcessor = resultProcessor;
    this.exceptionProcessor = exceptionProcessor;
  }

  public void success(RESULT result) {
    if (resultProcessor != null) {
      resultProcessor.postEvent(result);
    }
  }

  public void error(Exception result) {
    if (exceptionProcessor != null) {
      exceptionProcessor.postEvent(result);
    }
  }

  public PARAM getParam() {
    return param;
  }
}
