package org.coody.framework.minicat.web.adapter.dispat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.minicat.web.adapter.iface.CoodyParameterAdapter;


public class DispatAdapter {

	private static final Map<Class<?>, CoodyParameterAdapter> ADAPT_MAP=new ConcurrentHashMap<Class<?>, CoodyParameterAdapter>();
	
	
	
	public static CoodyParameterAdapter getAdapt(Class<?> clazz) throws InstantiationException, IllegalAccessException{
		if(ADAPT_MAP.containsKey(clazz)){
			return ADAPT_MAP.get(clazz);
		}
		CoodyParameterAdapter adapt=(CoodyParameterAdapter) clazz.newInstance();
		ADAPT_MAP.put(clazz, adapt);
		return adapt;
	}
}
