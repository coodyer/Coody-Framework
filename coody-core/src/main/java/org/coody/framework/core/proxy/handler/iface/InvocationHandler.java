package org.coody.framework.core.proxy.handler.iface;

import java.lang.reflect.Method;

import org.coody.framework.core.proxy.iface.Proxy;

public interface InvocationHandler {

	Object invoke(Proxy bean, Method method, Object[] args) throws Throwable;

}