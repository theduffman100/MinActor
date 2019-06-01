package com.l2o.minactor.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.l2o.minactor.ActorBroker;
import com.l2o.minactor.BaseActor;
import com.l2o.minactor.call.BaseCallResult;
import com.l2o.minactor.call.Call;
import com.l2o.minactor.call.CallImpl;
import com.l2o.minactor.call.CallWithParam;

/**
 * This base class can be derived to provide instances of an asynchronous
 * interface with calls being declared as synchronous (either returning a result
 * or suspending it and send the result later).
 * 
 * The asynchronous interface methods should return either CallResult or void.
 * 
 * Methods in the interface and sub-class must have the same name and parameter
 * types and be visible.
 * 
 * If an interface method returns void (making it impossible to hook exception
 * and timeout handlers) then the method will be called in a pure async fashion.
 * Otherwise the implementation can return void while the interface method
 * returns a CallResult, in which case the caller can hook exception and timeout
 * handlers.
 * 
 * The implementation can return the value after its execution by calling
 * suspendCall() and using the returned object later. In this case its return
 * value will be ignored (the implementation method can return void).
 *
 * @param <INTERFACE> The proxy interface. All methods in that proxy should
 *        return CallResults if the result is returned right away, or void if
 *        the result is to be sent later.
 */
public abstract class DelegateActor<INTERFACE> extends BaseActor<CallWithParam<Invocation, Object>>
    implements InvocationHandler {
  private Map<Method, Method> proxyMethodMap = new HashMap<>();
  private INTERFACE proxy;
  private CallWithParam<Invocation, Object> currentCall;
  private boolean currentCallSuspended = false;

  @SuppressWarnings("unchecked")
  public DelegateActor() {
    try {
      Class<?> itf = getInterface();
      Class<?> impl = getClass();
      for (Method itfMethod : itf.getDeclaredMethods()) {
        Method implMethod = impl.getDeclaredMethod(itfMethod.getName(), itfMethod.getParameterTypes());
        proxyMethodMap.put(itfMethod, implMethod);
      }
      proxy = (INTERFACE) Proxy.newProxyInstance(impl.getClassLoader(), new Class<?>[] { itf }, this);
    } catch (Exception ex) {
      throw new IllegalStateException("Cannot create proxy", ex);
    }
  }

  public INTERFACE get() {
    return proxy;
  }

  private Class<?> getInterface() {
    Class<?> c = getClass();
    while (c != null && c != DelegateActor.class) {
      Type genericSuperclass = c.getGenericSuperclass();
      if (genericSuperclass instanceof ParameterizedType) {
        ParameterizedType pt = (ParameterizedType) genericSuperclass;
        return (Class<?>) pt.getActualTypeArguments()[0];
      } else if (genericSuperclass instanceof Class) {
        c = (Class<?>) genericSuperclass;
      } else {
        break;
      }
    }
    throw new IllegalStateException("Cannot identify interface");
  }

  @Override
  public void handleEvent(CallWithParam<Invocation, Object> event) {
    try {
      currentCall = event;
      currentCallSuspended = false;
      Invocation invocation = event.getParam();
      Method method = invocation.getMethod();
      Object result = method.invoke(this, invocation.getParams());
      if (!currentCallSuspended && method.getReturnType() != Void.TYPE) {
        event.success(result);
      }
    } catch (Exception ex) {
      event.error(ex);
    } finally {
      currentCall = null;
      currentCallSuspended = false;
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Method implMethod = proxyMethodMap.get(method);
    if (method.getReturnType() == Void.TYPE) {
      // Interface can declare pure async methods returning void
      postEvent(new CallImpl<>(new Invocation(implMethod, args), null));
      return null;
    }
    ActorBroker currentBroker = ActorBroker.getCurrentBroker();
    if (currentBroker == null) {
      throw new IllegalStateException("Cannot use actor callback since caller is not an actor");
    }
    BaseCallResult<Object> result = new BaseCallResult<>(currentBroker);
    postEvent(result.getTask(new Invocation(implMethod, args)));
    return result;
  }

  protected Call<Object> getCurrentCall() {
    return currentCall;
  }

  /**
   * Gets the current call object to be able to send the result later. This
   * prevents the result from being sent immediately after the invocation.
   * 
   * @return The current call
   */
  protected Call<Object> suspendCall() {
    currentCallSuspended = true;
    return currentCall;
  }
}
