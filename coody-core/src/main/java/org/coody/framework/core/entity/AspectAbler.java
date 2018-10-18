package org.coody.framework.core.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.coody.framework.core.constant.AspectConstant;
import org.coody.framework.core.container.ThreadContainer;

import net.sf.cglib.proxy.MethodProxy;

@SuppressWarnings("serial")
public class AspectAbler extends BaseModel implements Cloneable {

	// 业务bean
	private Object bean;
	// 业务方法
	private Method method;
	// 代理
	private MethodProxy proxy;
	// 业务所在class
	private Class<?> clazz;
	// 子切面
	private AspectAbler childAbler;
	// 切面方法
	private Method aspectMethod;
	// 切面bean
	private Object aspectBean;

	private Boolean masturbation = true;

	public Boolean getMasturbation() {
		return masturbation;
	}

	public void setMasturbation(Boolean masturbation) {
		this.masturbation = masturbation;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public MethodProxy getProxy() {
		return proxy;
	}

	public void setProxy(MethodProxy proxy) {
		this.proxy = proxy;
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

		if (masturbation) {
			if (childAbler == null) {
				return proxy.invokeSuper(bean, params);
			}
			point.setAbler(childAbler);
			return childAbler.getAspectMethod().invoke(childAbler.getAspectBean(), point);
		}
		String aspectFlag = AspectConstant.THREAD_ENCRYPT_FLAG + "_" + clazz.getName();
		try {
			if (childAbler == null) {
				return proxy.invokeSuper(bean, params);
			}
			point.setAbler(childAbler);
			if (ThreadContainer.get(aspectFlag) != null) {
				return childAbler.invoke(point, params);
			}
			ThreadContainer.set(aspectFlag, this);
			return childAbler.getAspectMethod().invoke(childAbler.getAspectBean(), point);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} finally {
			ThreadContainer.remove(aspectFlag);
		}
	}

}
