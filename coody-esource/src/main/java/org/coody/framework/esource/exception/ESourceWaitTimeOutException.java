package org.coody.framework.esource.exception;

@SuppressWarnings("serial")
public class ESourceWaitTimeOutException extends ESourceException {

	public ESourceWaitTimeOutException(String msg) {
		super(msg);
	}

	public ESourceWaitTimeOutException(String msg, Exception e) {
		super(msg, e);
	}
}
