package org.coody.framework.logged.exception;

@SuppressWarnings("serial")
public class LoggedException extends RuntimeException {

	public LoggedException() {
		super();
	}

	public LoggedException(String msg) {
		super(msg);
	}

	public LoggedException(String msg, Exception e) {
		super(msg, e);
	}

}
