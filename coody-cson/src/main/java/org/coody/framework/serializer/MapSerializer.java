package org.coody.framework.serializer;

import java.util.Map;

import org.coody.framework.container.ThreadSetContainer;
import org.coody.framework.serializer.iface.AbstractSerializer;

public class MapSerializer extends AbstractSerializer<Map<?, ?>> {

	@Override
	public String adapt(Map<?, ?> target) {
		if (target == null) {
			return null;
		}
		if (target.isEmpty()) {
			return "{}";
		}
		if (!ThreadSetContainer.add(target)) {
			return "{}";
		}
		StringBuilder jsonBuilder = new StringBuilder();
		for (Object key : target.keySet()) {
			Object value = target.get(key);
			if (value == null) {
				continue;
			}
			jsonBuilder.append(",").append(serialize(key)).append(":").append(serialize(value));
		}
		jsonBuilder.append("}");
		String json = jsonBuilder.toString();
		while (json.startsWith(",")) {
			json = json.substring(1, json.length());
		}
		return "{" + json;
	}

}
