package org.coody.framework.serializer.adapter;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class ArrayAdapter extends AbstractAdapter<Object[]> {

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
			jsonBuilder.append(",").append(AbstractAdapter.serializer(line));
		}
		jsonBuilder.append("]");
		String json = jsonBuilder.toString();
		while (json.startsWith(",")) {
			json = json.substring(1, json.length());
		}
		return "[" + json;
	}

}
