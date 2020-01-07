package org.coody.framework;

import org.coody.framework.entity.CsonArray;
import org.coody.framework.entity.CsonObject;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.parser.JsonParser;
import org.coody.framework.serializer.iface.AbstractSerializer;

public class Cson {

	public static String toJson(Object object) {
		return AbstractSerializer.serialize(object);
	}

	@SuppressWarnings("unchecked")
	public static <T> T toObject(String json, Class<?> clazz) {
		ObjectWrapper wrapper = JsonParser.parseObject(json, clazz);
		return (T) wrapper.getObject();
	}

	public static CsonObject toCsonObject(String json) {
		return JsonParser.parseCsonObject(json);
	}

	public static CsonArray toCsonArray(String json) {
		return JsonParser.parseCsonArray(json);
	}
}
