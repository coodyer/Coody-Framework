package org.coody.framework.jdbc.exception;

import org.coody.framework.jdbc.exception.base.EdbcException;

/**
 * 
 * @author Coody
 * @date 2018年11月13日
 */
@SuppressWarnings("serial")
public class JdbcBuilderException extends EdbcException{

	
	public JdbcBuilderException(String msg) {
		super(msg);
	}
}
