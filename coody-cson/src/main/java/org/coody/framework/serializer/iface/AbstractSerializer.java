package org.coody.framework.serializer.iface;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

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
import org.coody.framework.serializer.StringSerializer;
import org.coody.framework.serializer.ThrowableSerializer;

public abstract class AbstractSerializer<T> {

	private static AbstractSerializer<Date> dateSerializer = new DateSerializer();

	private static AbstractSerializer<Number> numberSerializer = new NumberSerializer();

	private static AbstractSerializer<String> stringSerializer = new StringSerializer();

	private static AbstractSerializer<Object[]> arraySerializer = new ArraySerializer();

	private static AbstractSerializer<Collection<?>> collectionSerializer = new CollectionSerializer();

	private static AbstractSerializer<Iterator<?>> iteratorSerializer = new IteratorSerializer();

	private static AbstractSerializer<Throwable> throwableSerializer = new ThrowableSerializer();

	private static LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer();

	private static BooleanSerializer booleanSerializer = new BooleanSerializer();

	private static MapSerializer mapSerializer = new MapSerializer();

	private static ObjectSerializer objectSerializer = new ObjectSerializer();

	private static EnumSerializer enumSerializer = new EnumSerializer();

	public static String serializer(Object target) {
		if (target == null) {
			return null;
		}
		if (Date.class.isAssignableFrom(target.getClass())) {
			return dateSerializer.adapt((Date) target);
		}
		if (Number.class.isAssignableFrom(target.getClass())) {
			return numberSerializer.adapt((Number) target);
		}
		if (Boolean.class.isAssignableFrom(target.getClass())) {
			return booleanSerializer.adapt((Boolean) target);
		}
		if (String.class.isAssignableFrom(target.getClass())) {
			return stringSerializer.adapt((String) target);
		}
		if (Enum.class.isAssignableFrom(target.getClass())) {
			return enumSerializer.adapt((Enum<?>) target);
		}
		if (target.getClass().isPrimitive()) {
			return target + "";
		}
		if (target.getClass().isArray()) {
			return arraySerializer.adapt((Object[]) target);
		}
		if (ChronoLocalDateTime.class.isAssignableFrom(target.getClass())) {
			return localDateTimeSerializer.adapt((ChronoLocalDateTime<?>) target);
		}
		if (Collection.class.isAssignableFrom(target.getClass())) {
			return collectionSerializer.adapt((Collection<?>) target);
		}
		if (Map.class.isAssignableFrom(target.getClass())) {
			return mapSerializer.adapt((Map<?, ?>) target);
		}
		if (Iterator.class.isAssignableFrom(target.getClass())) {
			return iteratorSerializer.adapt((Iterator<?>) target);
		}
		if (Throwable.class.isAssignableFrom(target.getClass())) {
			return throwableSerializer.adapt((Throwable) target);
		}
		return objectSerializer.adapt(target);
	}

	protected abstract String adapt(T target);

}
