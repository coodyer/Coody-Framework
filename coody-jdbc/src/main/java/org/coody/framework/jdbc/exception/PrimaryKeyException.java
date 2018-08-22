package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

@SuppressWarnings("serial")
public class PrimaryKeyException extends EdbcException{

	public PrimaryKeyException(){
		super();
	}
	
	public PrimaryKeyException(String msg){
		super(msg);
	}
	
	public PrimaryKeyException(String msg,Exception e){
		super(msg, e);
	}
}
