package org.coody.framework.serializer;

import java.util.Iterator;

import org.coody.framework.container.ThreadSetContainer;
import org.coody.framework.serializer.iface.AbstractSerializer;

public class IteratorSerializer extends AbstractSerializer<Iterator<?>> {

	@Override
	public String adapt(Iterator<?> target) {
		if (target == null) {
			return null;
		}
		if (!target.hasNext()) {
			return "[]";
		}
		if (!ThreadSetContainer.add(target)) {
			return "[]";
		}
		StringBuilder jsonBuilder = new StringBuilder("");
		Object value = null;
		while (target.hasNext()) {
			value = target.next();
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
