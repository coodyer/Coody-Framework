package org.coody.framework.checker.exception;

@SuppressWarnings("serial")
public class FormatErrorException extends RuntimeException {

	private String msg;

	public FormatErrorException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
