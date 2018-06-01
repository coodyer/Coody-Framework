package org.coody.framework.exception;

@SuppressWarnings("serial")
public class BeanInitException extends IcopException{


	public BeanInitException(Class<?> clazz){
		super("Bean初始化失败:"+clazz.getName());
	}
}
