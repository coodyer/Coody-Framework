package org.coody.framework.exception;

@SuppressWarnings("serial")
public class BadFormatException extends CsonException {

	public BadFormatException(String msg) {
		super(msg);
	}
}
