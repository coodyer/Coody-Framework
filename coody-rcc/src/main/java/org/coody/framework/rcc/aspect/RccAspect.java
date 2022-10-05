package org.coody.framework.rcc.aspect;

import java.lang.reflect.Method;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.rcc.annotation.RccClient;
import org.coody.framework.rcc.caller.RccSendCaller;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.instance.RccKeepInstance;

@AutoBuild
public class RccAspect implements InitBeanFace {

	@AutoBuild
	RccSendCaller caller;

	/**
	 * RCC远程调用
	 * 
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass = RccClient.class)
	public Object remoteCall(AspectPoint point) throws Throwable {
		// AOP获取方法执行信息
		Method method = point.getAbler().getMethod();
		// 获得调用参数
		Object[] parameter = point.getParams();
		// 序列化参数
		byte[] data = RccKeepInstance.serialer.serialize(parameter);
		// 远程调用
		Class<?> clazz = PropertUtil.getClass(method);
		RccClient rcc = clazz.getAnnotation(RccClient.class);
		if (rcc == null) {
			throw new RccException("未找到RccClient标记 >>" + clazz.getName());
		}
		String path = String.format("%s/%s/%s", rcc.name(), rcc.path(), MethodSignUtil.getGeneralKeyByMethod(method));

		byte[] result = caller.send(path, data);
		if (result == null) {
			return null;
		}
		return RccKeepInstance.serialer.unSerialize(result);
	}

	@Override
	public void init() throws Exception {
	}

}
