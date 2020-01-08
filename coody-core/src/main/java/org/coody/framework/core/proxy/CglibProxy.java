package org.coody.framework.core.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coody.framework.core.constant.FrameworkConstant;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.container.InterceptContainer;
import org.coody.framework.core.exception.BeanInitException;
import org.coody.framework.core.exception.MappedExecutableException;
import org.coody.framework.core.model.AspectAbler;
import org.coody.framework.core.model.AspectEntity;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.util.AntUtil;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.core.util.ParameterNameUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor {

	public Object getProxy(Class<?> clazz) {
		return getProxy(clazz, null);
	}

	public Object getProxy(Class<?> clazz, Map<String, Object> parameterMap) {
		try {
			Integer modifier = clazz.getModifiers();
			if (Modifier.isAbstract(modifier)) {
				return null;
			}
			if (Modifier.isInterface(modifier)) {
				return null;
			}
			MappedConstructor mappedConstructor = getConstructor(clazz, parameterMap);
			if (!hasProxy(clazz)) {
				try {
					return mappedConstructor.getConstructor().newInstance(mappedConstructor.getParameters());
				} catch (Exception e) {
					throw new BeanInitException(clazz, e);
				}
			}
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(clazz);
			enhancer.setCallback(this);
			if (StringUtil.isNullOrEmpty(mappedConstructor.getTypes())) {
				return enhancer.create();
			}
			return enhancer.create(mappedConstructor.getTypes(), mappedConstructor.getParameters());
		} catch (Exception e) {
			throw new BeanInitException(clazz, e);
		}

	}

	public MappedConstructor getConstructor(Class<?> clazz, Map<String, Object> parameterMap) {
		if (StringUtil.isNullOrEmpty(parameterMap)) {
			MappedConstructor mappedConstructor = new MappedConstructor();
			for (Constructor<?> constructor : clazz.getConstructors()) {
				if (constructor.getParameterCount() > 0) {
					continue;
				}
				mappedConstructor.setConstructor(constructor);
			}
			if (mappedConstructor.getConstructor() == null) {
				throw new MappedExecutableException(clazz, parameterMap.keySet());
			}
			return mappedConstructor;
		}
		Map<Executable, List<String>> executableParameters = ParameterNameUtil.getExecutableParameters(clazz);
		if (StringUtil.isNullOrEmpty(executableParameters)) {
			throw new MappedExecutableException(clazz, parameterMap.keySet());
		}
		List<String> inputParameters = new ArrayList<String>(parameterMap.keySet());
		Collections.sort(inputParameters);

		checkExecutable: for (Executable executable : executableParameters.keySet()) {
			if (!(executable instanceof Constructor)) {
				continue checkExecutable;
			}
			List<String> defParameters = executableParameters.get(executable);
			if (defParameters.size() != parameterMap.size()) {
				continue checkExecutable;
			}
			List<String> tempParameters = new ArrayList<String>(defParameters);
			Collections.sort(tempParameters);
			for (int i = 0; i < tempParameters.size(); i++) {
				if (!tempParameters.get(i).equals(inputParameters.get(i))) {
					continue checkExecutable;
				}
			}
			MappedConstructor mappedConstructor = new MappedConstructor();
			mappedConstructor.setConstructor((Constructor<?>) executable);
			mappedConstructor.setTypes(executable.getParameterTypes());
			Object[] parameterValues = new Object[defParameters.size()];
			for (int i = 0; i < defParameters.size(); i++) {
				Object value = parameterMap.get(defParameters.get(i));
				parameterValues[i] = PropertUtil.parseValue(value, mappedConstructor.getTypes()[i]);
			}
			mappedConstructor.setParameters(parameterValues);
			return mappedConstructor;
		}
		throw new MappedExecutableException(clazz, parameterMap.keySet());
	}

	private boolean hasProxy(Class<?> clazz) {
		Set<Method> methods = PropertUtil.getMethods(clazz);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
			for (Class aspectAnnotationClazz : aspectEntity.getAnnotationClass()) {
				Annotation annotation = PropertUtil.getAnnotation(method, aspectAnnotationClazz);
				if (annotation != null) {
					return true;
				}
			}
		}
		return false;
	}

	// 拦截父类所有方法的调用
	@Override
	public Object intercept(Object bean, Method method, Object[] params, MethodProxy proxy) throws Throwable {
		if (!InterceptContainer.INTERCEPT_MAP.containsKey(method)) {
			// 该方法不存在AOP拦截
			return proxy.invokeSuper(bean, params);
		}
		// 获取拦截该方法的切面
		AspectAbler point = getPoint(bean, method, proxy);
		if (point == null) {
			// 该方法不存在AOP拦截
			return proxy.invokeSuper(bean, params);
		}
		AspectPoint aspectAble = new AspectPoint(point, params);
		return aspectAble.invoke();
	}

	private AspectAbler getPoint(Object bean, Method method, MethodProxy proxy) {
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
		abler.setMasturbation(aspectEntity.getOwnIntercept());
		AspectAbler childAbler = parseAspect(abler, invokeMethods);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		AspectAbler childAbler = parseAspect(basePoint, invokeAspects);
		if (childAbler != null) {
			abler.setChildAbler(childAbler);
			return abler;
		}
		return abler;
	}

	@SuppressWarnings("serial")
	public static class MappedConstructor extends BaseModel {

		private Constructor<?> constructor;

		private Class<?>[] types;

		private Object[] parameters;

		public Constructor<?> getConstructor() {
			return constructor;
		}

		public void setConstructor(Constructor<?> constructor) {
			this.constructor = constructor;
		}

		public Class<?>[] getTypes() {
			return types;
		}

		public void setTypes(Class<?>[] executable) {
			this.types = executable;
		}

		public Object[] getParameters() {
			return parameters;
		}

		public void setParameters(Object[] parameters) {
			this.parameters = parameters;
		}

	}

}
