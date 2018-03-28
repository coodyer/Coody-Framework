package org.coody.framework.container;

import org.coody.framework.aspect.CacheAspect;
import org.coody.framework.aspect.LoggerAspect;
import org.coody.framework.aspect.TransactedAspect;

public class BuiltContainer {

	
	public static final Class<?> [] initAspect={CacheAspect.class,TransactedAspect.class,LoggerAspect.class};
}
