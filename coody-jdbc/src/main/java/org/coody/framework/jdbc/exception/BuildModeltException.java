package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

@SuppressWarnings("serial")
public class BuildModeltException extends EdbcException{

	public BuildModeltException(){
		super();
	}
	
	public BuildModeltException(String msg){
		super(msg);
	}
	
	public BuildModeltException(String msg,Exception e){
		super(msg, e);
	}
}
