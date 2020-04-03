package org.coody.framework.rcc.aspect;

import java.lang.reflect.Method;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.rcc.annotation.RccClient;
import org.coody.framework.rcc.caller.RccSendCaller;
import org.coody.framework.rcc.serialer.JDKSerialer;
import org.coody.framework.rcc.serialer.iface.RccSerialer;

@AutoBuild
public class RccAspect implements InitBeanFace {

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
	@Around(annotationClass = RccClient.class)
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

	@Override
	public void init() throws Exception {
		if (serialer == null) {
			serialer = new JDKSerialer();
		}
	}

	public void setSerialer(RccSerialer serialer) {
		this.serialer = serialer;
	}

	public void setCaller(RccSendCaller caller) {
		this.caller = caller;
	}
	
	

}
