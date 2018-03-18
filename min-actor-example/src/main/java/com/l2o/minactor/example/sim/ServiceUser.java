package com.l2o.minactor.example.sim;

import com.l2o.minactor.guice.GuiceBaseActor;

class ServiceUser extends GuiceBaseActor<String> {
    void start(ServiceQueue queue, long time) {
        System.out.println(toString() + "Arriving at " + getBroker().getCurrentTime() + " - count: " + ++SimExample.count);
        queue.use(time).success(this::onServed);
    }
    private void onServed(Boolean used) {
        System.out.println(toString() + "Leaving at " + getBroker().getCurrentTime() + " - count: " + --SimExample.count);
        dispose();
    }
}