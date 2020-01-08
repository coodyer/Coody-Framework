package org.coody.framework.core.util.magic.iface;

public interface DynamicContainer {

	public <T> T get(String field);
	
	public boolean set(String field,Object value);
}
