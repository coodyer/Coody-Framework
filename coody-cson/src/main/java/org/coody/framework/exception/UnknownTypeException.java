package org.coody.framework.exception;

@SuppressWarnings("serial")
public class UnknownTypeException extends CsonException {

	public UnknownTypeException(String msg) {
		super(msg);
	}
}
