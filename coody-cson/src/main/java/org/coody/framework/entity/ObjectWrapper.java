package org.coody.framework.entity;

public class ObjectWrapper<T> {

	private T object;

	private int offset;

	public ObjectWrapper() {
		super();
	}

	public ObjectWrapper(T object) {
		super();
		this.object = object;
	}

	public ObjectWrapper(T object, int offset) {
		super();
		this.object = object;
		this.offset = offset;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
