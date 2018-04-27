package org.coody.framework.container;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.point.AspectPoint;

public class InterceptContainer {

	/**
	 * key拦截方法，value拦截器的方法
	 */
	public static final Map<Method, Set<Method>> INTERCEPT_MAP = new ConcurrentHashMap<Method, Set<Method>>();

	public static final Map<Method, AspectPoint> METHOD_INTERCEPT_MAP = new ConcurrentHashMap<Method, AspectPoint>();
}
