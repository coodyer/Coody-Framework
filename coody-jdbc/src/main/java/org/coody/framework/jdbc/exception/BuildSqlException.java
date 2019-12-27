package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

@SuppressWarnings("serial")
public class BuildSqlException extends EdbcException{

	public BuildSqlException(){
		super();
	}
	
	public BuildSqlException(String msg){
		super(msg);
	}
	
	public BuildSqlException(String msg,Exception e){
		super(msg, e);
	}
}
