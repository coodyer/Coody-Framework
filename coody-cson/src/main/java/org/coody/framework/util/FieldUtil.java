package org.coody.framework.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coody.framework.annotation.CsonDateFormat;
import org.coody.framework.annotation.CsonIgnore;
import org.coody.framework.entity.JsonFieldEntity;

public class FieldUtil {

	private static final Map<Class<?>, List<JsonFieldEntity>> FIELD_MAP = new ConcurrentHashMap<Class<?>, List<JsonFieldEntity>>();

	public static JsonFieldEntity getDeclaredField(Class<?> clazz, String fieldName) {
		List<JsonFieldEntity> fields = getDeclaredFields(clazz);
		if (fields == null) {
			return null;
		}
		for (JsonFieldEntity jsonFieldEntity : fields) {
			if (jsonFieldEntity.getField().getName().equals(fieldName)) {
				return jsonFieldEntity;
			}
		}
		return null;
	}

	/**
	 * value值转换为对应的类型
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseValue(Object value, Class<?> clazz) {
		try {
			if (value == null) {
				if (clazz.isPrimitive()) {
					if (boolean.class.isAssignableFrom(clazz)) {
						return false;
					}
					if (byte.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (char.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (short.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (int.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (float.class.isAssignableFrom(clazz)) {
						return 0f;
					}
					if (long.class.isAssignableFrom(clazz)) {
						return 0L;
					}
					if (double.class.isAssignableFrom(clazz)) {
						return 0d;
					}
				}
				return value;
			}
			if (clazz.isAssignableFrom(value.getClass())) {
				return value;
			}
			if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) {
				value = Integer.valueOf(value.toString());
				return value;
			}
			if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
				value = Float.valueOf(value.toString());
				return value;
			}
			if (Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
				value = Long.valueOf(value.toString());
				return value;
			}
			if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) {
				value = Double.valueOf(value.toString());
				return value;
			}
			if (Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz)) {
				value = Short.valueOf(value.toString());
				return value;
			}
			if (Byte.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz)) {
				value = Byte.valueOf(value.toString());
				return value;
			}
			if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
				value = ("true".equals(value.toString()) || "1".equals(value.toString())) ? true : false;
				return value;
			}
			if (String.class.isAssignableFrom(clazz)) {
				value = value.toString();
				return value;
			}
			if (Date.class.isAssignableFrom(clazz)) {
				value = parseDate(value);
				return value;
			}
			return value;
		} catch (Exception e) {

			return null;
		}
	}

	private static Boolean isMatcher(String val, String matcher) {
		Pattern p = Pattern.compile(matcher);
		Matcher m = p.matcher(val);
		return m.matches();
	}

	private static Date parseDate(Object value) {
		if (value == null) {
			return null;
		}
		try {
			Class<?> clazz = value.getClass();
			if (clazz.isPrimitive()) {
				if (value.toString().length() == 13) {
					return new Date(Long.valueOf(value.toString()));
				}
			}
			if (isMatcher(value.toString(), "\\d{13}")) {
				value = new Date(Long.valueOf(value.toString()));
				return (Date) value;
			}
			if (isMatcher(value.toString(), "\\d{8}")) {
				value = new SimpleDateFormat("yyyyMMdd").parse(value.toString());
				return (Date) value;
			}
			if (isMatcher(value.toString(), "\\d{10}")) {
				value = new SimpleDateFormat("yyyyMMddHH").parse(value.toString());
				return (Date) value;
			}
			if (isMatcher(value.toString(), "\\d{12}")) {
				value = new SimpleDateFormat("yyyyMMddHHmm").parse(value.toString());
				return (Date) value;
			}
			if (isMatcher(value.toString(), "\\d{14}")) {
				value = new SimpleDateFormat("yyyyMMddHHmmss").parse(value.toString());
				return (Date) value;
			}
			if (isMatcher(value.toString(), "\\d{17}")) {
				value = new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(value.toString());
				return (Date) value;
			}
			if (isMatcher(value.toString(), "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) {
				value = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
				return (Date) value;
			}
			if (isMatcher(value.toString(),
					"^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*")) {
				value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.toString());
				return (Date) value;
			}
			return (Date) value;
		} catch (Exception e) {
			return null;
		}
	}

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
