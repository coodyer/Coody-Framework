package org.coody.framework.rcc.aspect;

import java.lang.reflect.Method;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.point.AspectAble;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.rcc.annotation.RccInterface;
import org.coody.framework.rcc.caller.RccSendCaller;
import org.coody.framework.rcc.serialer.iface.RccSerialer;

@AutoBuild
public class RccAspect {

	@AutoBuild
	RccSerialer serialer;
	@AutoBuild
	RccSendCaller caller;

	/**
	 * RCC远程调用
	 * 
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass = RccInterface.class)
	public Object remoteCall(AspectAble able) throws Throwable {
		// AOP获取方法执行信息
		Method method = able.getPoint().getMethod();
		// 获得调用参数
		Object[] params = able.getParams();
		// 序列化参数
		byte[] data = serialer.serialize(params);
		// 远程调用
		String methodKey = MethodSignUtil.getMethodKey(able.getPoint().getClazz(), method);
		byte[] result = caller.send(methodKey, data);
		if (result == null) {
			return null;
		}
		return serialer.unSerialize(result);
	}
}
