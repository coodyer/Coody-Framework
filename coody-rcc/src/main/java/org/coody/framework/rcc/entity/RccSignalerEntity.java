package org.coody.framework.rcc.entity;

import java.io.ByteArrayOutputStream;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.rcc.exception.RccException;

@SuppressWarnings("serial")
public class RccSignalerEntity extends BaseModel {

	private RccInstance rcc;

	private byte[] data;

	public RccInstance getRcc() {
		return rcc;
	}

	public void setRcc(RccInstance rcc) {
		this.rcc = rcc;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] builder() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String remark = "{" + data.length + "}";
		try {
			byteArrayOutputStream.write(remark.getBytes());
			byteArrayOutputStream.write(data);
			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			throw new RccException("数据写入错误");
		}
	}
	
}
