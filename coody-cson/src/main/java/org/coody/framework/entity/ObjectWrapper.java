package org.coody.framework.entity;

public class ObjectWrapper<T> {

	private T object;

	private int length;

	public ObjectWrapper() {
		super();
	}

	public ObjectWrapper(T object) {
		super();
		this.object = object;
	}

	public ObjectWrapper(T object, int length) {
		super();
		this.object = object;
		this.length = length;
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

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
