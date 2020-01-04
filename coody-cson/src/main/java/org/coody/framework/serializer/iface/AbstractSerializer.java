package org.coody.framework.serializer.iface;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.coody.framework.serializer.ArraySerializer;
import org.coody.framework.serializer.BooleanSerializer;
import org.coody.framework.serializer.CollectionSerializer;
import org.coody.framework.serializer.DateSerializer;
import org.coody.framework.serializer.EnumSerializer;
import org.coody.framework.serializer.IteratorSerializer;
import org.coody.framework.serializer.LocalDateTimeSerializer;
import org.coody.framework.serializer.MapSerializer;
import org.coody.framework.serializer.NumberSerializer;
import org.coody.framework.serializer.ObjectSerializer;
import org.coody.framework.serializer.PrimitiveSerializer;
import org.coody.framework.serializer.StringSerializer;
import org.coody.framework.serializer.ThrowableSerializer;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public abstract class AbstractSerializer<T> {

	private static AbstractSerializer<Date> dateSerializer = new DateSerializer();

	private static AbstractSerializer<Number> numberSerializer = new NumberSerializer();

	private static AbstractSerializer<String> stringSerializer = new StringSerializer();

	private static AbstractSerializer<Object[]> arraySerializer = new ArraySerializer();

	private static AbstractSerializer<Collection<?>> collectionSerializer = new CollectionSerializer();

	private static AbstractSerializer<Iterator<?>> iteratorSerializer = new IteratorSerializer();

	private static AbstractSerializer<Throwable> throwableSerializer = new ThrowableSerializer();

	private static AbstractSerializer<ChronoLocalDateTime<?>> localDateTimeSerializer = new LocalDateTimeSerializer();

	private static AbstractSerializer<Boolean> booleanSerializer = new BooleanSerializer();

	private static AbstractSerializer<Map<?, ?>> mapSerializer = new MapSerializer();

	private static AbstractSerializer<Object> objectSerializer = new ObjectSerializer();

	private static AbstractSerializer<Enum<?>> enumSerializer = new EnumSerializer();

	private static AbstractSerializer<Object> primitiveSerializer = new PrimitiveSerializer();

	private static final Map<Class<?>, AbstractSerializer<?>> SERIALIZER_CONTAINER = new ConcurrentHashMap<Class<?>, AbstractSerializer<?>>() {
		{

			put(Integer.class, numberSerializer);
			put(Long.class, numberSerializer);
			put(Double.class, numberSerializer);
			put(Float.class, numberSerializer);
			put(BigDecimal.class, numberSerializer);
			put(AtomicLong.class, numberSerializer);
			put(AtomicInteger.class, numberSerializer);
			put(AtomicBoolean.class, booleanSerializer);
			put(ArrayList.class, collectionSerializer);
			put(LinkedList.class, collectionSerializer);
			put(Vector.class, collectionSerializer);
			put(String.class, stringSerializer);
			put(Boolean.class, booleanSerializer);
			put(Enum.class, enumSerializer);
			put(LocalDateTime.class, localDateTimeSerializer);
			put(Date.class, dateSerializer);
		}
	};

	public static String serialize(Object target) {
		if (target == null) {
			return null;
		}
		AbstractSerializer serializer = getSerializer(target.getClass());
		return serializer.adapt(target);
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
			if (String.class.isAssignableFrom(clazz)) {
				serializer = stringSerializer;
				return serializer;
			}
			if (Number.class.isAssignableFrom(clazz)) {
				serializer = numberSerializer;
				return serializer;
			}
			if (Date.class.isAssignableFrom(clazz)) {
				serializer = dateSerializer;
				return serializer;
			}
			
			if (Boolean.class.isAssignableFrom(clazz)) {
				serializer = booleanSerializer;
				return serializer;
			}
			if (Enum.class.isAssignableFrom(clazz)) {
				serializer = enumSerializer;
				return serializer;
			}
			if (clazz.isArray()) {
				serializer = arraySerializer;
				return serializer;
			}
			if (ChronoLocalDateTime.class.isAssignableFrom(clazz)) {
				serializer = localDateTimeSerializer;
				return serializer;
			}
			if (Collection.class.isAssignableFrom(clazz)) {
				serializer = collectionSerializer;
				return serializer;
			}
			if (Map.class.isAssignableFrom(clazz)) {
				serializer = mapSerializer;
				return serializer;
			}
			if (Iterator.class.isAssignableFrom(clazz)) {
				serializer = iteratorSerializer;
				return serializer;
			}
			if (Throwable.class.isAssignableFrom(clazz)) {
				serializer = throwableSerializer;
				return serializer;
			}
			serializer = objectSerializer;
			return serializer;
		} finally {
			SERIALIZER_CONTAINER.put(clazz, serializer);
		}
	}

	protected abstract String adapt(T target);

}
