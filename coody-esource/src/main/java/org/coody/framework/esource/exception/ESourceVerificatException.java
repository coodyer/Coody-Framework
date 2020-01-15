package org.coody.framework.esource.exception;

@SuppressWarnings("serial")
public class ESourceVerificatException extends RuntimeException {

	public ESourceVerificatException() {
		super();
	}

	public ESourceVerificatException(String msg) {
		super(msg);
	}

	public ESourceVerificatException(String msg, Exception e) {
		super(msg, e);
	}
}
