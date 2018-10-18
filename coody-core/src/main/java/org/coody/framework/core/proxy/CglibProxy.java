package org.coody.framework.core.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coody.framework.core.constant.FrameworkConstant;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.container.InterceptContainer;
import org.coody.framework.core.entity.AspectAbler;
import org.coody.framework.core.entity.AspectEntity;
import org.coody.framework.core.entity.AspectPoint;
import org.coody.framework.core.util.AntUtil;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor {



	public Object getProxy(Class<?> clazz)  {
		Integer modifier = clazz.getModifiers();
		if (Modifier.isAbstract(modifier)) {
			return null;
		}
		if (Modifier.isInterface(modifier)) {
			return null;
		}
		if (!isNeedProxyMethods(clazz)) {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		return enhancer.create();
	}

	private boolean isNeedProxyMethods(Class<?> clazz) {
		Set<Method> methods=PropertUtil.getMethods(clazz);
		if (StringUtil.isNullOrEmpty(methods)) {
			return false;
		}
		boolean needProxy = false;
		for (Method method : methods) {
			for (List<AspectEntity> aspectEntitys : FrameworkConstant.ASPECT_MAP.values()) {
				for (AspectEntity aspectEntity : aspectEntitys) {
					if (!needProxy(clazz, aspectEntity, method)) {
						continue;
					}
					if (InterceptContainer.INTERCEPT_MAP.containsKey(method)) {
						InterceptContainer.INTERCEPT_MAP.get(method).add(aspectEntity);
						needProxy = true;
						continue;
					}
					Set<AspectEntity> aspectMethods = new HashSet<AspectEntity>();
					aspectMethods.add(aspectEntity);
					InterceptContainer.INTERCEPT_MAP.put(method, aspectMethods);
				}
				needProxy = true;
			}
		}
		return needProxy;
	}

	private boolean needProxy(Class<?> clazz, AspectEntity aspectEntity, Method method) {
		/**
		 * 判断类名是否满足条件
		 */
		if (!StringUtil.isNullOrEmpty(aspectEntity.getClassMappath())) {
			if (!AntUtil.isAntMatch(clazz.getName(), aspectEntity.getClassMappath())) {
				return false;
			}
		}
		/**
		 * 判断方法名是否满足条件
		 */
		if (!StringUtil.isNullOrEmpty(aspectEntity.getMethodMappath())) {
			if (!AntUtil.isAntMatch(MethodSignUtil.getMethodUnionKey(method), aspectEntity.getMethodMappath())) {
				return false;
			}
		}
		/**
		 * 判断注解是否满足条件
		 */
		if (!StringUtil.isNullOrEmpty(aspectEntity.getAnnotationClass())) {
			Annotation[] annotations = method.getAnnotations();
			if (StringUtil.isNullOrEmpty(annotations)) {
				return false;
			}
			List<Class<?>> annotationClazzs = new ArrayList<Class<?>>();
			for (Annotation annotation : annotations) {
				annotationClazzs.add(annotation.annotationType());
			}
			for (Class<?> aspectAnnotationClazz : aspectEntity.getAnnotationClass()) {
				if (!annotationClazzs.contains(aspectAnnotationClazz)) {
					return false;
				}
			}
		}
		return true;
	}

	// 拦截父类所有方法的调用
	@Override
	public Object intercept(Object bean, Method method, Object[] params, MethodProxy proxy) throws Throwable {
		if (!InterceptContainer.INTERCEPT_MAP.containsKey(method)) {
			//该方法不存在AOP拦截
			return proxy.invokeSuper(bean, params);
		}
		//获取拦截该方法的切面
		AspectAbler point = getMethodPoint(bean, method, proxy);
		if (point == null) {
			//该方法不存在AOP拦截
			return proxy.invokeSuper(bean, params);
		}
		AspectPoint aspectAble=new AspectPoint(point,params);
		return aspectAble.invoke();
	}

	private AspectAbler getMethodPoint(Object bean, Method method, MethodProxy proxy) {
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
		abler.setBean(bean);
		abler.setClazz(bean.getClass());
		abler.setMethod(method);
		abler.setProxy(proxy);
		abler.setMasturbation(aspectEntity.getMasturbation());
		AspectAbler childAbler = parseAspect(abler, invokeMethods);
		if (childAbler != null) {
			abler.setChildAbler(childAbler);
		}
		AspectAbler turboPoint=new AspectAbler();
		turboPoint.setChildAbler(abler);
		turboPoint.setMasturbation(abler.getMasturbation());
		turboPoint.setClazz(bean.getClass());
		InterceptContainer.METHOD_INTERCEPT_MAP.put(method, turboPoint);
		return turboPoint;
	}

	private AspectAbler parseAspect(AspectAbler basePoint, List<AspectEntity> invokeAspects) {
		if (StringUtil.isNullOrEmpty(invokeAspects)) {
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
		abler.setProxy(basePoint.getProxy());
		abler.setMasturbation(aspectEntity.getMasturbation());
		AspectAbler childAbler = parseAspect(basePoint, invokeAspects);
		if (childAbler != null) {
			abler.setChildAbler(childAbler);
			return abler;
		}
		return abler;
	}
}
