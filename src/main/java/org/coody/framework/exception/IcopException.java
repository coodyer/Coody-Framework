package org.coody.framework.exception;

@SuppressWarnings("serial")
public class IcopException extends Exception{

	public IcopException(){
		super();
	}
	
	public IcopException(String msg){
		super(msg);
	}
	
	public IcopException(String msg,Exception e){
		super(msg, e);
	}
}
