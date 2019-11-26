package org.coody.framework.mail.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class MailException extends CoodyException {

	public MailException() {
		super();
	}

	public MailException(String msg) {
		super(msg);
	}

	public MailException(String msg, Exception e) {
		super(msg, e);
	}
}
