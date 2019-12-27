package org.coody.framework.core.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class UnsafeException extends CoodyException{
	public UnsafeException(){
		super();
	}
	
	public UnsafeException(String msg){
		super(msg);
	}
	
	public UnsafeException(String msg,Exception e){
		super(msg, e);
	}
}
