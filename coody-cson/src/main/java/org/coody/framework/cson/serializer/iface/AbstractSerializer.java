package org.coody.framework.cson.serializer.iface;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.coody.framework.cson.serializer.ArraySerializer;
import org.coody.framework.cson.serializer.BooleanSerializer;
import org.coody.framework.cson.serializer.CollectionSerializer;
import org.coody.framework.cson.serializer.DateSerializer;
import org.coody.framework.cson.serializer.EnumSerializer;
import org.coody.framework.cson.serializer.IteratorSerializer;
import org.coody.framework.cson.serializer.LocalDateTimeSerializer;
import org.coody.framework.cson.serializer.MapSerializer;
import org.coody.framework.cson.serializer.NumberSerializer;
import org.coody.framework.cson.serializer.ObjectSerializer;
import org.coody.framework.cson.serializer.PrimitiveSerializer;
import org.coody.framework.cson.serializer.StringSerializer;
import org.coody.framework.cson.serializer.ThrowableSerializer;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractSerializer<T> {

	private static AbstractSerializer<Object> objectSerializer = new ObjectSerializer();

	private static AbstractSerializer<Object> primitiveSerializer = new PrimitiveSerializer();

	private static final Map<Class<?>, AbstractSerializer<?>> SERIALIZER_CONTAINER = new HashMap<Class<?>, AbstractSerializer<?>>();

	private static final Map<Class<?>, AbstractSerializer<?>> SOURCE_CONTAINER = new LinkedHashMap<Class<?>, AbstractSerializer<?>>();

	static {
		addSerializer(Number.class, new NumberSerializer());
		addSerializer(String.class, new StringSerializer());
		addSerializer(Date.class, new DateSerializer());
		addSerializer(ChronoLocalDateTime.class, new LocalDateTimeSerializer());
		addSerializer(Throwable.class, new ThrowableSerializer());
		addSerializer(Boolean.class, new BooleanSerializer());
		addSerializer(Collection.class, new CollectionSerializer());
		addSerializer(Iterator.class, new IteratorSerializer());
		addSerializer(Map.class, new MapSerializer());
		addSerializer(Enum.class, new EnumSerializer());
		addSerializer(Object[].class, new ArraySerializer());
	}

	public static String serialize(Object target) {
		if (target == null) {
			return null;
		}
		AbstractSerializer serializer = getSerializer(target.getClass());
		return serializer.adapt(target);

	}

	public static synchronized void addSerializer(Class<?> clazz, AbstractSerializer<?> serializer) {
		SOURCE_CONTAINER.put(clazz, serializer);
		SERIALIZER_CONTAINER.put(clazz, serializer);
	}

	private static AbstractSerializer<?> getSerializer(Class<?> clazz) {
		AbstractSerializer<?> serializer = SERIALIZER_CONTAINER.get(clazz);
		if (serializer != null) {
			return serializer;
		}
		try {
			if (clazz.isPrimitive()) {
				serializer = primitiveSerializer;
				return serializer;
			}
			for (Class<?> key : SOURCE_CONTAINER.keySet()) {
				if (key.isAssignableFrom(clazz)) {
					serializer = SOURCE_CONTAINER.get(key);
					return serializer;
				}
			}
			serializer = objectSerializer;
			return serializer;
		} finally {
			SERIALIZER_CONTAINER.put(clazz, serializer);
		}
	}

	protected abstract String adapt(T target);
}
