package org.coody.framework.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.annotation.CsonDateFormat;
import org.coody.framework.annotation.CsonIgnore;
import org.coody.framework.entity.JsonFieldEntity;

public class FieldUtil {

	private static final Map<Class<?>, List<JsonFieldEntity>> FIELD_MAP = new ConcurrentHashMap<Class<?>, List<JsonFieldEntity>>();

	public static List<JsonFieldEntity> getDeclaredFields(Class<?> clazz) {
		if (clazz.getName().equals(Object.class.getName())) {
			return null;
		}
		List<JsonFieldEntity> fields = FIELD_MAP.get(clazz);
		if (fields != null) {
			return fields;
		}
		Field[] fieldArgs = clazz.getDeclaredFields();
		if (fieldArgs.length == 0) {
			return null;
		}
		fields = new ArrayList<JsonFieldEntity>(fieldArgs.length);
		for (Field f : fieldArgs) {
			f.setAccessible(true);
			JsonFieldEntity jsonFieldEntity = new JsonFieldEntity();
			jsonFieldEntity.setField(f);
			jsonFieldEntity.setIsIgonre(false);
			CsonIgnore ignore = f.getAnnotation(CsonIgnore.class);
			if (ignore != null) {
				jsonFieldEntity.setIsIgonre(true);
			}
			CsonDateFormat format = f.getAnnotation(CsonDateFormat.class);
			if (format != null) {
				jsonFieldEntity.setFormat(format.value());
			}
			fields.add(jsonFieldEntity);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			List<JsonFieldEntity> temp = getDeclaredFields(superClass);
			if (temp != null) {
				fields.addAll(temp);
			}
		}
		FIELD_MAP.put(clazz, fields);
		return fields;
	}
}
