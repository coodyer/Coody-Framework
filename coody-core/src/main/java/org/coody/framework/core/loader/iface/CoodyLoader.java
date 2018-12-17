package org.coody.framework.core.loader.iface;

import java.util.Set;

public interface CoodyLoader {

	public void doLoader(Set<Class<?>> clazzs) throws Exception;
}
