package org.coody.framework.box.exception;

@SuppressWarnings("serial")
public class BeanNotFoundException extends Exception{

	
	public BeanNotFoundException(String msg){
		super(msg);
	}
	
	public BeanNotFoundException(String msg,Exception e){
		super(msg, e);
	}
}
