package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

@SuppressWarnings("serial")
public class GetConnectionException extends EdbcException{

	public GetConnectionException(){
		super();
	}
	
	public GetConnectionException(String msg){
		super(msg);
	}
	
	public GetConnectionException(String msg,Exception e){
		super(msg, e);
	}
}
