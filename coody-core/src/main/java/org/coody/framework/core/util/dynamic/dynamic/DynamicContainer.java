package org.coody.framework.core.util.dynamic.dynamic;

public interface DynamicContainer {

	public <T> T get(String field);

	public boolean set(String field, Object value);
}
