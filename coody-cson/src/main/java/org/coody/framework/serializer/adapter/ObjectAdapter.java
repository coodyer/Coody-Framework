package org.coody.framework.serializer.adapter;

import java.util.List;

import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class ObjectAdapter extends AbstractAdapter<Object> {

	@Override
	public String adapt(Object target) {
		if (target == null) {
			return null;
		}
		List<FieldEntity> fields = PropertUtil.getBeanFields(target);
		if (fields == null || fields.isEmpty()) {
			return "";
		}
		StringBuilder jsonBuilder = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			FieldEntity field = fields.get(i);
			Object value = field.getFieldValue();
			if (value == null) {
				continue;
			}
			jsonBuilder.append(",").append("\"").append(field.getFieldName()).append("\"").append(":")
					.append(serializer(value));
			continue;
		}
		jsonBuilder.append("}");
		String json = jsonBuilder.toString();
		while (json.startsWith(",")) {
			json = json.substring(1, json.length());
		}
		return "{" + json;
	}

}
