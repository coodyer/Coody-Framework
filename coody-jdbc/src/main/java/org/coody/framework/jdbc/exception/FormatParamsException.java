package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

@SuppressWarnings("serial")
public class FormatParamsException extends EdbcException{

	public FormatParamsException(){
		super();
	}
	
	public FormatParamsException(String msg){
		super(msg);
	}
	
	public FormatParamsException(String msg,Exception e){
		super(msg, e);
	}
}
