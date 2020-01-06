package org.coody.framework.entity;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class CsonArray extends ArrayList<Object> {

	private int offset;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
