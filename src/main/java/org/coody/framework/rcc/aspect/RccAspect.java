package org.coody.framework.rcc.aspect;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.point.AspectPoint;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.rcc.annotation.RccService;
import org.coody.framework.rcc.caller.RccCaller;
import org.coody.framework.rcc.serialer.iface.RccSerialer;

@InitBean
public class RccAspect {

	@Resource
	RccSerialer serialer;
	@Resource
	RccCaller caller;

	/**
	 * RCC远程调用
	 * 
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass = RccService.class)
	public Object remoteCall(AspectPoint wrapper) throws Throwable {
		// AOP获取方法执行信息
		Method method = wrapper.getMethod();
		// 获得调用参数
		Object[] params = wrapper.getParams();
		// 序列化参数
		byte[] data = serialer.serialize(params);
		// 远程调用
		String methodKey = MethodSignUtil.getMethodKey(wrapper.getClazz(), method);
		byte[] result = caller.doCall(methodKey, data);
		if (result == null) {
			return null;
		}
		return serialer.unSerialize(result);
	}
}
