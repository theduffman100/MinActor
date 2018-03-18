package com.l2o.minactor.future;

import java.util.concurrent.Future;

import com.l2o.minactor.call.CallResult;

/**
 * An actor that can receive FutureCalls, wait for futures to be ready and send back the result
 */
public interface FutureKeeper {
    <RESULT>CallResult<RESULT> waitFor(Future<RESULT> future);
}
