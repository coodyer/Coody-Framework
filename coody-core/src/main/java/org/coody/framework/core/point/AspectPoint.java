package org.coody.framework.core.point;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.coody.framework.core.constant.AspectConstant;
import org.coody.framework.core.container.ThreadContainer;
import org.coody.framework.core.entity.BaseModel;

import net.sf.cglib.proxy.MethodProxy;

@SuppressWarnings("serial")
public class AspectPoint extends BaseModel implements Cloneable {

	// 业务bean
	private Object bean;
	// 业务方法
	private Method method;
	// 代理
	private MethodProxy proxy;
	// 业务所在class
	private Class<?> clazz;
	// 子切面
	private AspectPoint childPoint;
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

	public AspectPoint getChildPoint() {
		return childPoint;
	}

	public void setChildPoint(AspectPoint childPoint) {
		this.childPoint = childPoint;
	}

	public Object getAspectBean() {
		return aspectBean;
	}

	public void setAspectBean(Object aspectBean) {
		this.aspectBean = aspectBean;
	}

	public Object invoke(AspectAble aspectAble, Object[] params) throws Throwable {

		if (masturbation) {
			if (childPoint == null) {
				return proxy.invokeSuper(bean, params);
			}
			aspectAble.setPoint(childPoint);
			return childPoint.getAspectMethod().invoke(childPoint.getAspectBean(), aspectAble);
		}
		String aspectFlag = AspectConstant.THREAD_ENCRYPT_FLAG + "_" + clazz.getName();
		try {
			if (childPoint == null) {
				return proxy.invokeSuper(bean, params);
			}
			aspectAble.setPoint(childPoint);
			if (ThreadContainer.get(aspectFlag) != null) {
				return childPoint.invoke(aspectAble, params);
			}
			ThreadContainer.set(aspectFlag, this);
			return childPoint.getAspectMethod().invoke(childPoint.getAspectBean(), aspectAble);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} finally {
			ThreadContainer.remove(aspectFlag);
		}
	}

}
