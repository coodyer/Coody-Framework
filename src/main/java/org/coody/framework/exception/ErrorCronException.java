package org.coody.framework.exception;

@SuppressWarnings("serial")
public class ErrorCronException extends Exception{

	public ErrorCronException(String msg){
		super(msg);
	}
	
	public ErrorCronException(String msg,Exception e){
		super(msg, e);
	}
}
