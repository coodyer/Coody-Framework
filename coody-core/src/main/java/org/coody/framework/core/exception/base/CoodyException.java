package org.coody.framework.core.exception.base;

@SuppressWarnings("serial")
public class CoodyException extends RuntimeException{

	public CoodyException(){
		super();
	}
	
	public CoodyException(String msg){
		super(msg);
	}
	
	public CoodyException(String msg,Exception e){
		super(msg, e);
	}
}
