package org.coody.framework;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class Cson {

	public static String toJson(Object object) {
		return AbstractSerializer.serialize(object);
	}

	public static <T> T toObject(String json, Class<?> clazz) {
		return null;
	}
}
