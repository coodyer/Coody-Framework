package org.coody.framework;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class Cson {

	public static String toJson(Object object) {
		return AbstractAdapter.serializer(object);
	}

	public static <T> T toObject(String json, Class<?> clazz) {
		return null;
	}
}
