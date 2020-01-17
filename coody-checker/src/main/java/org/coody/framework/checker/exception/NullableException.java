package org.coody.framework.checker.exception;

@SuppressWarnings("serial")
public class NullableException extends RuntimeException {

	private String msg;

	public NullableException(String msg) {
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
