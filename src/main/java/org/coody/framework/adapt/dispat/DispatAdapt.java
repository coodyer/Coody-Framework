package org.coody.framework.adapt.dispat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.adapt.iface.IcopParamsAdapt;


public class DispatAdapt {

	private static final Map<Class<?>, IcopParamsAdapt> adaptMap=new ConcurrentHashMap<Class<?>, IcopParamsAdapt>();
	
	
	
	public static IcopParamsAdapt getAdapt(Class<?> clazz) throws InstantiationException, IllegalAccessException{
		if(adaptMap.containsKey(clazz)){
			return adaptMap.get(clazz);
		}
		IcopParamsAdapt adapt=(IcopParamsAdapt) clazz.newInstance();
		adaptMap.put(clazz, adapt);
		return adapt;
	}
}
