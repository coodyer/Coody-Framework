package org.coody.framework.esource.exception;

@SuppressWarnings("serial")
public class ESourceCreateConnectionException extends RuntimeException {

	public ESourceCreateConnectionException() {
		super();
	}

	public ESourceCreateConnectionException(String msg) {
		super(msg);
	}

	public ESourceCreateConnectionException(String msg, Exception e) {
		super(msg, e);
	}
}
