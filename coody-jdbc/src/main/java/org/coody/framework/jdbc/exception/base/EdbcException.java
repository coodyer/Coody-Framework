package org.coody.framework.jdbc.exception.base;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class EdbcException extends CoodyException{

	public EdbcException(){
		super();
	}
	
	public EdbcException(String msg){
		super(msg);
	}
	
	public EdbcException(String msg,Exception e){
		super(msg, e);
	}
}
