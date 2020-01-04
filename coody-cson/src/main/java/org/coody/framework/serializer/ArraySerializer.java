package org.coody.framework.serializer;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class ArraySerializer extends AbstractSerializer<Object[]> {

	@Override
	public String adapt(Object[] target) {
		if (target == null) {
			return null;
		}
		if (target.length == 0) {
			return "[]";
		}
		StringBuilder jsonBuilder = new StringBuilder();
		for (Object line : target) {
			if (line == null) {
				jsonBuilder.append(",").append("null");
				continue;
			}
			jsonBuilder.append(",").append(AbstractSerializer.serializer(line));
		}
		jsonBuilder.append("]");
		String json = jsonBuilder.toString();
		while (json.startsWith(",")) {
			json = json.substring(1, json.length());
		}
		return "[" + json;
	}

}
