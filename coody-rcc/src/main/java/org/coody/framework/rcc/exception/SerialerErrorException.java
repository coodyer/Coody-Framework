package org.coody.framework.rcc.exception;

@SuppressWarnings("serial")
public class SerialerErrorException extends RccException {

	public SerialerErrorException(String msg, Exception e) {
		super(msg, e);
	}
}
