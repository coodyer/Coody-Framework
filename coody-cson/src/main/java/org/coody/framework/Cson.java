package org.coody.framework;

import org.coody.framework.entity.CsonArray;
import org.coody.framework.entity.CsonObject;
import org.coody.framework.parser.iface.AbstractParser;
import org.coody.framework.serializer.iface.AbstractSerializer;

public class Cson {

	public static String toJson(Object object) {
		return AbstractSerializer.serialize(object);
	}

	public static <T> T toObject(String json, Class<?> clazz) {
		return null;
	}

	public static CsonObject toCsonObject(String json) {
		return AbstractParser.parseCsonObject(json);
	}

	public static CsonArray toCsonArray(String json) {
		return AbstractParser.parseCsonArray(json);
	}
}
