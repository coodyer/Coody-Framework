package org.coody.framework.web.adapter.dispat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.util.UnsafeUtil;
import org.coody.framework.web.adapter.iface.CoodyParameterAdapter;

public class DispatAdapter {

	private static final Map<Class<?>, CoodyParameterAdapter> ADAPT_MAP = new ConcurrentHashMap<Class<?>, CoodyParameterAdapter>();

	public static CoodyParameterAdapter getAdapt(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		if (ADAPT_MAP.containsKey(clazz)) {
			return ADAPT_MAP.get(clazz);
		}
		CoodyParameterAdapter adapt = (CoodyParameterAdapter) UnsafeUtil.createInstance(clazz);
		ADAPT_MAP.put(clazz, adapt);
		return adapt;
	}
}
