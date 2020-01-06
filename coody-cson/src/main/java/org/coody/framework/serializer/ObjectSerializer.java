package org.coody.framework.serializer;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.List;

import org.coody.framework.entity.JsonFieldEntity;
import org.coody.framework.serializer.iface.AbstractSerializer;
import org.coody.framework.util.DateFormatUtils;
import org.coody.framework.util.FieldUtil;

public class ObjectSerializer extends AbstractSerializer<Object> {

	@Override
	public String adapt(Object target) {
		if (target == null) {
			return null;
		}
		List<JsonFieldEntity> fields = FieldUtil.getDeclaredFields(target.getClass());
		if (fields == null || fields.isEmpty()) {
			return "";
		}
		StringBuilder jsonBuilder = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			JsonFieldEntity field = fields.get(i);
			if (field.getIsIgonre()) {
				continue;
			}
			Object value = null;
			try {
				value = field.getField().get(target);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (value == null) {
				continue;
			}
			jsonBuilder.append(",").append("\"").append(field.getField().getName()).append("\"").append(":");
			if (field.getFormat() != null) {
				if (Date.class.isAssignableFrom(field.getField().getType())
						|| ChronoLocalDateTime.class.isAssignableFrom(field.getField().getType())) {
					Long timeMillis = Long.valueOf(serialize(value));
					jsonBuilder.append(serialize(DateFormatUtils.format(new Date(timeMillis), field.getFormat())));
					continue;
				}
			}
			jsonBuilder.append(serialize(value));
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
