package org.coody.framework.core.entity;

import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("serial")
public class AspectEntity extends BaseModel {

	private Class<?>[] annotationClass;

	private String methodMappath;

	private String classMappath;
	
	private Method aspectInvokeMethod;
	
	private Boolean masturbation;
	
	private Class<?> aspectClazz;
	
	
	
	

	
	
	
	public Class<?> getAspectClazz() {
		return aspectClazz;
	}

	public void setAspectClazz(Class<?> aspectClazz) {
		this.aspectClazz = aspectClazz;
	}

	public Boolean getMasturbation() {
		return masturbation;
	}

	public void setMasturbation(Boolean masturbation) {
		this.masturbation = masturbation;
	}

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

	@Override
	public String toString() {
		return "AspectEntity [annotationClass=" + Arrays.toString(annotationClass) + ", methodMappath=" + methodMappath
				+ ", classMappath=" + classMappath + ", aspectInvokeMethod=" + aspectInvokeMethod + "]";
	}

	
}
