package org.coody.framework.esource.exception;

@SuppressWarnings("serial")
public class ESourceCloseException extends RuntimeException {

	public ESourceCloseException() {
		super();
	}

	public ESourceCloseException(String msg) {
		super(msg);
	}

	public ESourceCloseException(String msg, Exception e) {
		super(msg, e);
	}
}
