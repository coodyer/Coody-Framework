package org.coody.framework.container;

import org.coody.framework.aspect.CacheAspect;
import org.coody.framework.aspect.LoggerAspect;
import org.coody.framework.aspect.TransactedAspect;
import org.coody.framework.wrapper.IcopRequestWrapper;
import org.coody.framework.wrapper.IcopResponseWrapper;

public class BuiltContainer {

	
	public static final Class<?> [] INIT_BEAN={CacheAspect.class,TransactedAspect.class,LoggerAspect.class,IcopRequestWrapper.class,IcopResponseWrapper.class};
}
