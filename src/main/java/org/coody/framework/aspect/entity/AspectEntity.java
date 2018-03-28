package org.coody.framework.aspect.entity;

import java.lang.reflect.Method;

import org.coody.framework.entity.BaseModel;

@SuppressWarnings("serial")
public class AspectEntity extends BaseModel {

	private Class<?>[] annotationClass;

	private String methodMappath;

	private String classMappath;
	
	private Method aspectInvokeMethod;
	
	

	
	public Method getAspectInvokeMethod() {
		return aspectInvokeMethod;
	}

	public void setAspectInvokeMethod(Method aspectInvokeMethod) {
		this.aspectInvokeMethod = aspectInvokeMethod;
	}

	public Class<?>[] getAnnotationClass() {
		return annotationClass;
	}

	public void setAnnotationClass(Class<?>[] annotationClass) {
		this.annotationClass = annotationClass;
	}

	public String getMethodMappath() {
		return methodMappath;
	}

	public void setMethodMappath(String methodMappath) {
		this.methodMappath = methodMappath;
	}

	public String getClassMappath() {
		return classMappath;
	}

	public void setClassMappath(String classMappath) {
		this.classMappath = classMappath;
	}

}
