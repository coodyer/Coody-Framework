package org.coody.framework.core.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class BeanInitException extends CoodyException{


	public BeanInitException(Class<?> clazz){
		super("Bean初始化失败 >>"+clazz.getName());
	}
	
	public BeanInitException(Class<?> clazz,Exception e){
		super("Bean初始化失败 >>"+clazz.getName(),e);
	}
}
