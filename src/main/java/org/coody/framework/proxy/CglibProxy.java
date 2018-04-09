package org.coody.framework.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.aspect.entity.AspectEntity;
import org.coody.framework.constant.FrameworkConstant;
import org.coody.framework.container.BeanContainer;
import org.coody.framework.point.AspectPoint;
import org.coody.framework.util.AntUtil;
import org.coody.framework.util.AspectUtil;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor {

	/**
	 * key拦截方法，value拦截器的方法
	 */
	public static final Map<Method, Set<Method>> interceptMap = new ConcurrentHashMap<Method, Set<Method>>();
	
	public static final Map<Method, AspectPoint> methodInterceptMap = new ConcurrentHashMap<Method, AspectPoint>();

	public Object getProxy(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		Integer modifier = clazz.getModifiers();
		if (Modifier.isAbstract(modifier)) {
			return null;
		}
		if (Modifier.isInterface(modifier)) {
			return null;
		}
		if (!isNeedProxyMethods(clazz)) {
			return clazz.newInstance();
		}
		Enhancer enhancer = new Enhancer();
		// 设置需要创建子类的类
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		return enhancer.create();
	}

	private boolean isNeedProxyMethods(Class<?> clazz) {
		if(StringUtil.isNullOrEmpty(clazz.getDeclaredMethods())){
			return false;
		}
		boolean needProxy=false;
		for(Method method:clazz.getDeclaredMethods()){
			for(AspectEntity aspectEntity:FrameworkConstant.aspectMap.values()){
				if(!needProxy(clazz, aspectEntity, method)){
					continue;
				}
				if (interceptMap.containsKey(method)) {
					interceptMap.get(method).add(aspectEntity.getAspectInvokeMethod());
					needProxy= true;
					continue;
				}
				Set<Method> aspectMethods = new HashSet<Method>();
				aspectMethods.add(aspectEntity.getAspectInvokeMethod());
				interceptMap.put(method, aspectMethods);
				needProxy= true;
			}
		}
		return needProxy;
	}

	private boolean needProxy(Class<?> clazz,AspectEntity aspectEntity,Method method){
		/**
		 * 判断类名是否满足条件
		 */
		if(!StringUtil.isNullOrEmpty(aspectEntity.getClassMappath())){
			if(!AntUtil.isAntMatch(clazz.getName(), aspectEntity.getClassMappath())){
				return false;
			}
		}
		/**
		 * 判断方法名是否满足条件
		 */
		if(!StringUtil.isNullOrEmpty(aspectEntity.getMethodMappath())){
			if(!AntUtil.isAntMatch(AspectUtil.getMethodUnionKey(method), aspectEntity.getMethodMappath())){
				return false;
			}
		}
		/**
		 * 判断注解是否满足条件
		 */
		if(!StringUtil.isNullOrEmpty(aspectEntity.getAnnotationClass())){
			Annotation[] annotations=method.getAnnotations();
			if(StringUtil.isNullOrEmpty(annotations)){
				return false;
			}
			List<Class<?>> annotationClazzs=new ArrayList<Class<?>>();
			for(Annotation annotation:annotations){
				annotationClazzs.add(annotation.annotationType());
			}
			for(Class<?> aspectAnnotationClazz:aspectEntity.getAnnotationClass()){
				if(!annotationClazzs.contains(aspectAnnotationClazz)){
					return false;
				}
			}
		}
		return true;
	}
	
	// 拦截父类所有方法的调用
	public Object intercept(Object bean, Method method, Object[] params, MethodProxy proxy) throws Throwable {
		if (!interceptMap.containsKey(method)) {
			return proxy.invokeSuper(bean, params);
		}
		AspectPoint point =getMethodPoint(bean, method, proxy);
		if(point==null){
			return proxy.invokeSuper(bean, params);
		}
		point.setParams(params);
		return point.getAspectMethod().invoke(point.getAspectBean(), point);
	}

	private AspectPoint getMethodPoint(Object bean, Method method,  MethodProxy proxy){
		if(methodInterceptMap.containsKey(method)){
			return methodInterceptMap.get(method);
		}
		List<Method> invokeMethods = new ArrayList<Method>(interceptMap.get(method));
		Method aspectMethod = invokeMethods.get(0);
		invokeMethods.remove(0);
		Class<?> clazz = PropertUtil.getClass(aspectMethod);
		Object aspectBean = BeanContainer.getBean(clazz);
		AspectPoint point = new AspectPoint();
		point.setAspectBean(aspectBean);
		point.setAspectMethod(aspectMethod);
		point.setBean(bean);
		point.setClazz(bean.getClass());
		point.setMethod(method);
		point.setProxy(proxy);
		AspectPoint childPoint = parseAspect(point, invokeMethods);
		if (childPoint != null) {
			point.setChildPoint(childPoint);
		}
		methodInterceptMap.put(method, point);
		return point;
	}
	
	
	private AspectPoint parseAspect(AspectPoint basePoint, List<Method> invokeMethods) {
		if (StringUtil.isNullOrEmpty(invokeMethods)) {
			return null;
		}
		Method aspectMethod = invokeMethods.get(0);
		invokeMethods.remove(0);
		Class<?> clazz = PropertUtil.getClass(aspectMethod);
		Object aspectBean = BeanContainer.getBean(clazz);
		
		AspectPoint point = new AspectPoint();
		point.setAspectBean(aspectBean);
		point.setAspectMethod(aspectMethod);
		point.setBean(basePoint.getBean());
		point.setClazz(basePoint.getBean().getClass());
		point.setMethod(basePoint.getMethod());
		point.setProxy(basePoint.getProxy());
		AspectPoint childPoint = parseAspect(basePoint, invokeMethods);
		if (childPoint != null) {
			point.setChildPoint(childPoint);
			return point;
		}
		return point;
	}
}
