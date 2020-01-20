package org.coody.framework.cson.exception;

@SuppressWarnings("serial")
public class CsonException extends RuntimeException {

	public CsonException() {
		super();
	}

	public CsonException(String msg) {
		super(msg);
	}

	public CsonException(String msg, Exception e) {
		super(msg, e);
	}
}
