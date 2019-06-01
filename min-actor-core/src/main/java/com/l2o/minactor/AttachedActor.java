package com.l2o.minactor;

/**
 * Convenience interface that inherits from Actor (able to be sent async
 * events), EventHandler (able to process said events) and Attached (able to be
 * connected to a broker).
 */
public interface AttachedActor<EVENTTYPE> extends Actor<EVENTTYPE>, EventHandler<EVENTTYPE>, Attached {

}
