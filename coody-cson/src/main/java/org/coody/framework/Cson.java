package org.coody.framework;

import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.container.ThreadSetContainer;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.interpreter.iface.AbstractInterpreter;
import org.coody.framework.serializer.iface.AbstractSerializer;

public class Cson {

	public static String toJson(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return AbstractSerializer.serialize(object);
		} finally {
			ThreadSetContainer.clear();
		}
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		if (json == null) {
			return null;
		}
		ObjectWrapper<T> wrapper = AbstractInterpreter.interpreter(json, clazz);
		if (wrapper == null) {
			return null;
		}
		return (T) wrapper.getObject();
	}

	public static <T> T toObject(String json, TypeAdapter<T> type) {
		if (json == null) {
			return null;
		}
		ObjectWrapper<T> wrapper = AbstractInterpreter.interpreter(json, type);
		if (wrapper == null) {
			return null;
		}
		return (T) wrapper.getObject();
	}
}
