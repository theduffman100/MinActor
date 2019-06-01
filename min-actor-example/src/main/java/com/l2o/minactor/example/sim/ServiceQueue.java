package com.l2o.minactor.example.sim;

import com.google.inject.ProvidedBy;
import com.l2o.minactor.call.CallResult;

@ProvidedBy(ServiceQueueImpl.class)
public interface ServiceQueue {
  CallResult<Boolean> use(long time);
}
