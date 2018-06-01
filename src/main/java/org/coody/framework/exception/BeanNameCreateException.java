package org.coody.framework.exception;

@SuppressWarnings("serial")
public class BeanNameCreateException extends IcopException{


	public BeanNameCreateException(Class<?> clazz){
		super("BeanName创建失败:"+clazz.getName());
	}
}
