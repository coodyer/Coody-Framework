package org.coody.framework.rcc.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class RccException extends CoodyException{

	public RccException(){
		super();
	}
	
	public RccException(String msg){
		super(msg);
	}
	
	public RccException(String msg,Exception e){
		super(msg, e);
	}

}
