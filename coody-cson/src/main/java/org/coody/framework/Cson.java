package org.coody.framework;

import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.container.ThreadSetContainer;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.interpreter.iface.AbstractInterpreter;
import org.coody.framework.serializer.iface.AbstractSerializer;

public class Cson {

	public static String toJson(Object object) {
		try {
			return AbstractSerializer.serialize(object);
		} finally {
			ThreadSetContainer.clear();
		}
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		ObjectWrapper<T> wrapper = AbstractInterpreter.interpreter(json, clazz);
		return (T) wrapper.getObject();
	}

	public static <T> T toObject(String json, TypeAdapter<T> type) {
		ObjectWrapper<T> wrapper = AbstractInterpreter.interpreter(json, type);
		return (T) wrapper.getObject();
	}
}
