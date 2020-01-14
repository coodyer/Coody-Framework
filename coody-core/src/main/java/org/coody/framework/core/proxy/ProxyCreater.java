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
import org.coody.framework.core.container.InterceptContainer;
import org.coody.framework.core.exception.BeanInitException;
import org.coody.framework.core.exception.MappedExecutableException;
import org.coody.framework.core.model.AspectEntity;
import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.proxy.handler.CoodyInvocationHandler;
import org.coody.framework.core.proxy.handler.iface.InvocationHandler;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.ant.AntUtil;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.ParameterNameUtil;
import org.coody.framework.core.util.reflex.PropertUtil;

public class ProxyCreater {

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
					if (CommonUtil.isNullOrEmpty(parameterMap)) {
						return clazz.newInstance();
					}
					return mappedConstructor.getConstructor().newInstance(mappedConstructor.getParameters());
				} catch (Exception e) {
					throw new BeanInitException(clazz, e);
				}
			}
			InvocationHandler invocationHandler = new CoodyInvocationHandler();
			if (CommonUtil.isNullOrEmpty(parameterMap)) {
				return AsmProxy.newProxyInstance(clazz, invocationHandler);
			}
			return AsmProxy.newProxyInstance(clazz, mappedConstructor.getConstructor(), invocationHandler,
					mappedConstructor.getParameters());
		} catch (Exception e) {
			throw new BeanInitException(clazz, e);
		}

	}

	public MappedConstructor getConstructor(Class<?> clazz, Map<String, Object> parameterMap) {
		if (CommonUtil.isNullOrEmpty(parameterMap)) {
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
		if (CommonUtil.isNullOrEmpty(executableParameters)) {
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
		if (CommonUtil.isNullOrEmpty(methods)) {
			return false;
		}

		for (Method method : methods) {
			for (List<AspectEntity> aspectEntitys : FrameworkConstant.ASPECT_MAP.values()) {
				for (AspectEntity aspectEntity : aspectEntitys) {
					if (!needProxy(clazz, aspectEntity, method)) {
						continue;
					}
					if (InterceptContainer.INTERCEPT_MAP.containsKey(method)) {
						InterceptContainer.INTERCEPT_MAP.get(method).add(aspectEntity);
						return true;
					}
					Set<AspectEntity> aspectMethods = new HashSet<AspectEntity>();
					aspectMethods.add(aspectEntity);
					InterceptContainer.INTERCEPT_MAP.put(method, aspectMethods);
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean needProxy(Class<?> clazz, AspectEntity aspectEntity, Method method) {
		/**
		 * 判断类名是否满足条件
		 */
		if (!CommonUtil.isNullOrEmpty(aspectEntity.getClassMappath())) {
			if (!AntUtil.isAntMatch(clazz.getName(), aspectEntity.getClassMappath())) {
				return false;
			}
		}
		/**
		 * 判断方法名是否满足条件
		 */
		if (!CommonUtil.isNullOrEmpty(aspectEntity.getMethodMappath())) {
			if (!AntUtil.isAntMatch(MethodSignUtil.getMethodUnionKey(method), aspectEntity.getMethodMappath())) {
				return false;
			}
		}
		/**
		 * 判断注解是否满足条件
		 */
		if (!CommonUtil.isNullOrEmpty(aspectEntity.getAnnotationClass())) {
			Annotation[] annotations = method.getAnnotations();
			if (CommonUtil.isNullOrEmpty(annotations)) {
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
