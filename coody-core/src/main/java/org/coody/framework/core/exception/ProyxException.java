package org.coody.framework.core.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class ProyxException extends CoodyException {

	public ProyxException(String msg) {
		super(msg);
	}

	public ProyxException(String msg, Exception e) {
		super(msg, e);
	}
}
