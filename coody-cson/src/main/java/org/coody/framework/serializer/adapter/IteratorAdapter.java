package org.coody.framework.serializer.adapter;

import java.util.Iterator;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class IteratorAdapter extends AbstractAdapter<Iterator<?>> {

	@Override
	public String adapt(Iterator<?> target) {
		if (target == null) {
			return null;
		}
		if (!target.hasNext()) {
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
