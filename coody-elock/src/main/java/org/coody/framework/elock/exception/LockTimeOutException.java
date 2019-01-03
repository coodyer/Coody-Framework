package org.coody.framework.elock.exception;

@SuppressWarnings("serial")
public class LockTimeOutException extends InterruptedException{

	public LockTimeOutException(String error) {
		super(error);
	}
}
