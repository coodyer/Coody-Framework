package org.coody.framework.core.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.constant.AspectConstant;
import org.coody.framework.core.container.ThreadContainer;
import org.coody.framework.core.proxy.iface.Proxy;

@SuppressWarnings("serial")
public class AspectAbler extends BaseModel implements Cloneable {

	// 业务bean
	private Object bean;
	// 代理bean
	private Proxy proxy;
	// 业务方法
	private Method method;
	// 业务所在class
	private Class<?> clazz;
	// 子切面
	private AspectAbler childAbler;
	// 切面方法
	private Method aspectMethod;
	// 切面bean
	private Object aspectBean;
	// 注解值
	private Map<Class<? extends Annotation>, Object> annotationValueMap = new ConcurrentHashMap<Class<? extends Annotation>, Object>();

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public Map<Class<? extends Annotation>, Object> getAnnotationValueMap() {
		return annotationValueMap;
	}

	public void setAnnotationValueMap(Map<Class<? extends Annotation>, Object> annotationValueMap) {
		this.annotationValueMap = annotationValueMap;
	}

	public Object getBean() {
		return bean;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getAspectMethod() {
		return aspectMethod;
	}

	public void setAspectMethod(Method aspectMethod) {
		this.aspectMethod = aspectMethod;
	}

	public AspectAbler getChildAbler() {
		return childAbler;
	}

	public void setChildAbler(AspectAbler childAbler) {
		this.childAbler = childAbler;
	}

	public Object getAspectBean() {
		return aspectBean;
	}

	public void setAspectBean(Object aspectBean) {
		this.aspectBean = aspectBean;
	}

	public Object invoke(AspectPoint point, Object[] params) throws Throwable {

		String aspectFlag = AspectConstant.THREAD_ENCRYPT_FLAG + "_" + clazz.getName();
		try {
			if (childAbler == null) {
				return method.invoke(bean, params);
			}
			point.setAbler(childAbler);
			if (ThreadContainer.get(aspectFlag) != null) {
				return childAbler.invoke(point, params);
			}
			ThreadContainer.set(aspectFlag, this);
			return childAbler.getAspectMethod().invoke(childAbler.getAspectBean(), point);
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				throw ((InvocationTargetException) e).getTargetException();
			}
			throw e;
		} finally {
			ThreadContainer.remove(aspectFlag);
		}
	}

}
