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

	@SuppressWarnings("unchecked")
	public T getObject() {
		if (object != null && object instanceof String) {
			object = (T) object.toString().replace("\\r", "\r").replace("\\n", "\n");
		}
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
