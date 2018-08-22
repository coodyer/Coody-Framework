package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

@SuppressWarnings("serial")
public class ExecSqlException extends EdbcException{

	public ExecSqlException(){
		super();
	}
	
	public ExecSqlException(String msg){
		super(msg);
	}
	
	public ExecSqlException(String msg,Exception e){
		super(msg, e);
	}
}
