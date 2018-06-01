package org.coody.framework.core.container;

import org.coody.framework.cache.aspect.CacheAspect;
import org.coody.framework.cache.instance.LocalCache;
import org.coody.framework.core.aspect.LoggerAspect;
import org.coody.framework.mvc.wrapper.IcopRequestWrapper;
import org.coody.framework.mvc.wrapper.IcopResponseWrapper;
import org.coody.framework.orm.aspect.TransactedAspect;
import org.coody.framework.rcc.aspect.RccAspect;
import org.coody.framework.rcc.caller.RccCaller;
import org.coody.framework.rcc.serialer.KryoSerialer;

public class BuiltContainer {

	
	public static final Class<?>[] INIT_BEAN={
			CacheAspect.class,
			TransactedAspect.class,
			LoggerAspect.class,
			RccAspect.class,
			IcopRequestWrapper.class,
			IcopResponseWrapper.class,
			KryoSerialer.class,
			LocalCache.class,
			RccCaller.class
	};
}
