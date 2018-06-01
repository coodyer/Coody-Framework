package org.coody.framework.core.exception;

import org.coody.framework.core.exception.base.IcopException;

@SuppressWarnings("serial")
public class BeanConflictException extends IcopException{


	public BeanConflictException(String bean){
		super("Bean已经存在:"+bean);
	}
	
	public BeanConflictException(String bean,Exception e){
		super("Bean已经存在:"+bean, e);
	}
}
