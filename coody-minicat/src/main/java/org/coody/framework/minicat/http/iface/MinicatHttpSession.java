package org.coody.framework.minicat.http.iface;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface MinicatHttpSession {

	public Date getActiveTime();

	public void setActiveTime(Date activeTime);

	public int size();

	public boolean isEmpty();

	public boolean containsKey(Object key);

	public <T> T get(Object key);

	public Object put(String key, Object value);

	public Object remove(Object key);

	public void putAll(Map<? extends String, ? extends Object> m);

	public Set<String> keySet();

	public Collection<Object> values();

	public Set<java.util.Map.Entry<String, Object>> entrySet();

	public boolean containsValue(Object value);

	public void clear();

	public Map<String, Object> getMap();

}
