package org.coody.framework.serializer.adapter;

import java.util.Collection;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class CollectionAdapter extends AbstractAdapter<Collection<?>> {

	@Override
	public String adapt(Collection<?> target) {
		if (target == null) {
			return null;
		}
		if (target.size() == 0) {
			return "[]";
		}
		StringBuilder jsonBuilder = new StringBuilder("");
		for (Object value : target) {
			if (value == null) {
				jsonBuilder.append(",").append("null");
				continue;
			}
			jsonBuilder.append(",").append(AbstractAdapter.serializer(value));
		}
		jsonBuilder.append("]");
		String json = jsonBuilder.toString();
		while (json.startsWith(",")) {
			json = json.substring(1, json.length());
		}
		return "[" + json;
	}

}
