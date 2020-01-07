package org.coody.framework.instance.iface;

public interface CsonInstancer {

	<T> T createInstance(Class<T> clazz);
}
