package org.coody.framework;

import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.container.ThreadSetContainer;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.parser.iface.AbstractParser;
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
		ObjectWrapper<T> wrapper = AbstractParser.parser(json, clazz);
		return (T) wrapper.getObject();
	}

	public static <T> T toObject(String json, TypeAdapter<T> type) {
		ObjectWrapper<T> wrapper = AbstractParser.parser(json, type);
		return (T) wrapper.getObject();
	}
}
