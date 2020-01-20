package org.coody.framework.cson.convert;

import org.coody.framework.cson.util.FieldUtil;
import org.coody.framework.cson.util.GeneralUtil;

public class ValueConvert {

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, boolean isString) {
		if (GeneralUtil.isNullOrEmpty(value)) {
			return null;
		}
		if (value.getClass().getName().equals(String.class.getName())) {
			return (T) value;
		}
		if (isString) {
			return convert(value, String.class);
		}
		value = value.toString().trim();
		if (value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false")) {
			return convert(value, Boolean.class);
		}
		try {
			if (value.toString().contains(".")) {
				return convert(value, Double.class);
			}
			return convert(value, Long.class);
		} catch (Exception e) {
			return (T) value.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, Class<?> clazz) {
		if (clazz.getName().equals(String.class.getName())) {
			value = value.toString().replace("\\t", "\t").replace("\\n", "\n").replace("\\r", "\r").replace("\\0", "\0")
					.replace("\\\"", "\"");
			return (T) value;
		}
		return FieldUtil.parseValue(value, clazz);
	}

}
