package org.coody.framework.serializer;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.List;

import org.coody.framework.annotation.CsonDateFormat;
import org.coody.framework.annotation.CsonIgnore;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.DateUtils;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.serializer.iface.AbstractSerializer;

public class ObjectSerializer extends AbstractSerializer<Object> {

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
			CsonIgnore ignore = field.getAnnotation(CsonIgnore.class);
			if (ignore != null) {
				continue;
			}
			Object value = field.getFieldValue();
			if (value == null) {
				continue;
			}
			jsonBuilder.append(",").append("\"").append(field.getFieldName()).append("\"").append(":");
			if (Date.class.isAssignableFrom(field.getFieldType())
					|| ChronoLocalDateTime.class.isAssignableFrom(field.getFieldType())) {
				CsonDateFormat format = field.getSourceField().getAnnotation(CsonDateFormat.class);
				if (format != null && !format.value().trim().equals("")) {
					Long timeMillis = Long.valueOf(serializer(value));
					jsonBuilder.append(serializer(DateUtils.toString(new Date(timeMillis), format.value())));
					continue;
				}
			}
			jsonBuilder.append(serializer(value));
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
