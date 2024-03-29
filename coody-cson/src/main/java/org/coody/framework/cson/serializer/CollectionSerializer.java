package org.coody.framework.cson.serializer;

import java.util.Collection;

import org.coody.framework.cson.container.ThreadSetContainer;
import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class CollectionSerializer extends AbstractSerializer<Collection<?>> {

	@Override
	public String adapt(Collection<?> target) {
		if (target == null) {
			return null;
		}
		if (target.size() == 0) {
			return "[]";
		}
		if (!ThreadSetContainer.add(target)) {
			return "[]";
		}
		StringBuilder jsonBuilder = new StringBuilder("");
		for (Object value : target) {
			if (value == null) {
				jsonBuilder.append(",").append("null");
				continue;
			}
			jsonBuilder.append(",").append(AbstractSerializer.serialize(value));
		}
		jsonBuilder.append("]");
		String json = jsonBuilder.toString();
		while (json.startsWith(",")) {
			json = json.substring(1, json.length());
		}
		return "[" + json;
	}

}
