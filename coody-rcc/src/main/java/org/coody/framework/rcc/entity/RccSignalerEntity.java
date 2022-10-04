package org.coody.framework.rcc.entity;

import org.coody.framework.core.model.BaseModel;

@SuppressWarnings("serial")
public class RccSignalerEntity extends BaseModel {

	private RccInstance rcc;

	private byte[] data;

	private Exception exception;

	private int expireTime;

	public RccInstance getRcc() {
		return rcc;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public void setRcc(RccInstance rcc) {
		this.rcc = rcc;
	}

	public byte[] getData() {
		return data;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
