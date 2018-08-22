package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

@SuppressWarnings("serial")
public class BuildResultException extends EdbcException{
	
	public BuildResultException(){
		super();
	}
	
	public BuildResultException(String msg){
		super(msg);
	}
	
	public BuildResultException(String msg,Exception e){
		super(msg, e);
	}
}
