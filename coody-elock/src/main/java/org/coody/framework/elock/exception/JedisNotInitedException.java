package org.coody.framework.elock.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class JedisNotInitedException extends CoodyException{

	public JedisNotInitedException(){
		super();
	}
	
	public JedisNotInitedException(String msg){
		super(msg);
	}
	
	public JedisNotInitedException(String msg,Exception e){
		super(msg, e);
	}
}
