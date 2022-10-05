package org.coody.framework.logged.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class LoggedException extends CoodyException{

	public LoggedException(){
		super();
	}
	
	public LoggedException(String msg){
		super(msg);
	}
	
	public LoggedException(String msg,Exception e){
		super(msg, e);
	}

}
