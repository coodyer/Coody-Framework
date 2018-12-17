package org.coody.framework.web.adapt.dispat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.web.adapt.iface.CoodyParamsAdapt;


public class DispatAdapt {

	private static final Map<Class<?>, CoodyParamsAdapt> ADAPT_MAP=new ConcurrentHashMap<Class<?>, CoodyParamsAdapt>();
	
	
	
	public static CoodyParamsAdapt getAdapt(Class<?> clazz) throws InstantiationException, IllegalAccessException{
		if(ADAPT_MAP.containsKey(clazz)){
			return ADAPT_MAP.get(clazz);
		}
		CoodyParamsAdapt adapt=(CoodyParamsAdapt) clazz.newInstance();
		ADAPT_MAP.put(clazz, adapt);
		return adapt;
	}
}
