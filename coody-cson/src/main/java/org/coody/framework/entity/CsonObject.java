package org.coody.framework.entity;

import java.util.HashMap;

@SuppressWarnings("serial")
public class CsonObject extends HashMap<String, Object> {

	private int offset;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
