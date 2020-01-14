package org.coody.framework.core.proxy.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.container.InterceptContainer;
import org.coody.framework.core.model.AspectAbler;
import org.coody.framework.core.model.AspectEntity;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.proxy.handler.iface.InvocationHandler;
import org.coody.framework.core.proxy.iface.Proxy;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.reflex.PropertUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CoodyInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Proxy bean, Method method, Object[] args) throws Throwable {
		if (!InterceptContainer.INTERCEPT_MAP.containsKey(method)) {
			// 该方法不存在AOP拦截
			return method.invoke(bean.getTargetObject(), args);
		}
		// 获取拦截该方法的切面
		AspectAbler point = getPointer(bean, method);
		if (point == null) {
			// 该方法不存在AOP拦截
			return method.invoke(bean.getTargetObject(), args);
		}
		AspectPoint aspectAble = new AspectPoint(point, args);
		return aspectAble.invoke();
	}

	private AspectAbler getPointer(Proxy bean, Method method) {
		if (InterceptContainer.METHOD_INTERCEPT_MAP.containsKey(method)) {
			return InterceptContainer.METHOD_INTERCEPT_MAP.get(method);
		}
		List<AspectEntity> invokeMethods = new ArrayList<AspectEntity>(InterceptContainer.INTERCEPT_MAP.get(method));
		AspectEntity aspectEntity = invokeMethods.get(0);
		invokeMethods.remove(0);
		Object aspectBean = BeanContainer.getBean(aspectEntity.getAspectClazz());
		AspectAbler abler = new AspectAbler();
		abler.setAspectBean(aspectBean);
		abler.setAspectMethod(aspectEntity.getAspectInvokeMethod());
		abler.setBean(bean.getTargetObject());
		abler.setClazz(bean.getClass());
		abler.setMethod(method);
		abler.setMasturbation(aspectEntity.getOwnIntercept());
		AspectAbler childAbler = getAspecter(abler, invokeMethods);
		if (childAbler != null) {
			abler.setChildAbler(childAbler);
		}
		AspectAbler turboPoint = new AspectAbler();
		turboPoint.setChildAbler(abler);
		turboPoint.setMasturbation(abler.getMasturbation());
		turboPoint.setClazz(bean.getClass());
		InterceptContainer.METHOD_INTERCEPT_MAP.put(method, turboPoint);
		return turboPoint;
	}

	private AspectAbler getAspecter(AspectAbler basePoint, List<AspectEntity> invokeAspects) {
		if (CommonUtil.isNullOrEmpty(invokeAspects)) {
			return null;
		}
		AspectEntity aspectEntity = invokeAspects.get(0);
		invokeAspects.remove(0);
		Object aspectBean = BeanContainer.getBean(aspectEntity.getAspectClazz());

		AspectAbler abler = new AspectAbler();
		abler.setAspectBean(aspectBean);
		abler.setAspectMethod(aspectEntity.getAspectInvokeMethod());
		abler.setBean(basePoint.getBean());
		abler.setClazz(basePoint.getBean().getClass());
		abler.setMethod(basePoint.getMethod());
		abler.setMasturbation(aspectEntity.getOwnIntercept());
		if (aspectEntity.getAspectClazz() != null) {
			for (Class clazz : aspectEntity.getAnnotationClass()) {
				Annotation annotation = PropertUtil.getAnnotation(basePoint.getMethod(), clazz);
				if (annotation == null) {
					continue;
				}
				abler.getAnnotationValueMap().put(clazz, annotation);
			}
		}
		AspectAbler childAbler = getAspecter(basePoint, invokeAspects);
		if (childAbler != null) {
			abler.setChildAbler(childAbler);
			return abler;
		}
		return abler;
	}
}