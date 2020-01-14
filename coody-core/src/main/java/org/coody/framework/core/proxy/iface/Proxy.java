package org.coody.framework.core.proxy.iface;

import org.coody.framework.core.proxy.handler.iface.InvocationHandler;

public interface Proxy {

	public void setInvocationHandler(InvocationHandler invocationHandler);

	public InvocationHandler getInvocationHandler();

	public void setTargetObject(Object target);

	public Object getTargetObject();
}
