package org.coody.framework.core.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class BeanNameCreateException extends CoodyException{


	public BeanNameCreateException(Class<?> clazz){
		super("BeanName创建失败 >>"+clazz.getName());
	}
}
