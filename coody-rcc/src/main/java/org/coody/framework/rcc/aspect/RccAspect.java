package org.coody.framework.rcc.aspect;

import java.lang.reflect.Method;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.util.reflex.MethodSignUtil;
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
	public Object remoteCall(AspectPoint point) throws Throwable {
		// AOP获取方法执行信息
		Method method = point.getAbler().getMethod();
		// 获得调用参数
		Object[] params = point.getParams();
		// 序列化参数
		byte[] data = serialer.serialize(params);
		// 远程调用
		String methodKey = MethodSignUtil.getMethodUnionKey(method);
		byte[] result = caller.send(methodKey, data);
		if (result == null) {
			return null;
		}
		return serialer.unSerialize(result);
	}
}
