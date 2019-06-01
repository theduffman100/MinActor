package com.l2o.minactor.proxy;

import java.lang.reflect.Method;

class Invocation {
  private Method method;
  private Object[] params;

  public Invocation(Method method, Object[] params) {
    this.method = method;
    this.params = params;
  }

  Method getMethod() {
    return method;
  }

  Object[] getParams() {
    return params;
  }
}