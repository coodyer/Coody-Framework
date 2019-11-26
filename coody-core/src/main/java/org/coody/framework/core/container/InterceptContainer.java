package org.coody.framework.core.container;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.model.AspectAbler;
import org.coody.framework.core.model.AspectEntity;

public class InterceptContainer {

	/**
	 * key拦截方法，value拦截器的方法
	 */
	public static final Map<Method, Set<AspectEntity>> INTERCEPT_MAP = new ConcurrentHashMap<Method, Set<AspectEntity>>();

	public static final Map<Method, AspectAbler> METHOD_INTERCEPT_MAP = new ConcurrentHashMap<Method, AspectAbler>();
}
