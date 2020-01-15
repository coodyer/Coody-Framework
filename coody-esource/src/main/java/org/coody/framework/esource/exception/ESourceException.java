package org.coody.framework.esource.exception;

@SuppressWarnings("serial")
public class ESourceException extends RuntimeException {

	public ESourceException() {
		super();
	}

	public ESourceException(String msg) {
		super(msg);
	}

	public ESourceException(String msg, Exception e) {
		super(msg, e);
	}
}
