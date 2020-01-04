package org.coody.framework.serializer.adapter;

import java.util.Map;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class MapAdapter extends AbstractAdapter<Map<?, ?>> {

	@Override
	public String adapt(Map<?, ?> target) {
		if (target == null) {
			return null;
		}
		if (target.isEmpty()) {
			return "{}";
		}
		StringBuilder jsonBuilder = new StringBuilder();
		for (Object key : target.keySet()) {
			Object value = target.get(key);
			if (value == null) {
				continue;
			}
			jsonBuilder.append(",").append(serializer(key)).append(":").append(serializer(value));
		}
		jsonBuilder.append("}");
		String json = jsonBuilder.toString();
		while (json.startsWith(",")) {
			json = json.substring(1, json.length());
		}
		return "{" + json;
	}

}
