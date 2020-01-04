package org.coody.framework.serializer.adapter.iface;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.coody.framework.serializer.adapter.ArrayAdapter;
import org.coody.framework.serializer.adapter.BooleanAdapter;
import org.coody.framework.serializer.adapter.CollectionAdapter;
import org.coody.framework.serializer.adapter.DateAdapter;
import org.coody.framework.serializer.adapter.EnumAdapter;
import org.coody.framework.serializer.adapter.IteratorAdapter;
import org.coody.framework.serializer.adapter.LocalDateTimeAdapter;
import org.coody.framework.serializer.adapter.MapAdapter;
import org.coody.framework.serializer.adapter.NumberAdapter;
import org.coody.framework.serializer.adapter.ObjectAdapter;
import org.coody.framework.serializer.adapter.StringAdapter;
import org.coody.framework.serializer.adapter.ThrowableAdapter;

public abstract class AbstractAdapter<T> {

	private static AbstractAdapter<Date> dateAdapter = new DateAdapter();

	private static AbstractAdapter<Number> numberAdapter = new NumberAdapter();

	private static AbstractAdapter<String> stringAdapter = new StringAdapter();

	private static AbstractAdapter<Object[]> arrayAdapter = new ArrayAdapter();

	private static AbstractAdapter<Collection<?>> collectionAdapter = new CollectionAdapter();

	private static AbstractAdapter<Iterator<?>> iteratorAdapter = new IteratorAdapter();

	private static AbstractAdapter<Throwable> throwableAdapter = new ThrowableAdapter();

	private static LocalDateTimeAdapter localDateTimeAdapter = new LocalDateTimeAdapter();

	private static BooleanAdapter booleanAdapter = new BooleanAdapter();

	private static MapAdapter mapAdapter = new MapAdapter();

	private static ObjectAdapter objectAdapter = new ObjectAdapter();

	private static EnumAdapter enumAdapter = new EnumAdapter();

	public static String serializer(Object target) {
		if (target == null) {
			return null;
		}
		if (Date.class.isAssignableFrom(target.getClass())) {
			return dateAdapter.adapt((Date) target);
		}
		if (Number.class.isAssignableFrom(target.getClass())) {
			return numberAdapter.adapt((Number) target);
		}
		if (Boolean.class.isAssignableFrom(target.getClass())) {
			return booleanAdapter.adapt((Boolean) target);
		}
		if (String.class.isAssignableFrom(target.getClass())) {
			return stringAdapter.adapt((String) target);
		}
		if (Enum.class.isAssignableFrom(target.getClass())) {
			return enumAdapter.adapt((Enum<?>) target);
		}
		if (target.getClass().isPrimitive()) {
			return target + "";
		}
		if (target.getClass().isArray()) {
			return arrayAdapter.adapt((Object[]) target);
		}
		if (ChronoLocalDateTime.class.isAssignableFrom(target.getClass())) {
			return localDateTimeAdapter.adapt((ChronoLocalDateTime<?>) target);
		}
		if (Collection.class.isAssignableFrom(target.getClass())) {
			return collectionAdapter.adapt((Collection<?>) target);
		}
		if (Map.class.isAssignableFrom(target.getClass())) {
			return mapAdapter.adapt((Map<?, ?>) target);
		}
		if (Iterator.class.isAssignableFrom(target.getClass())) {
			return iteratorAdapter.adapt((Iterator<?>) target);
		}
		if (Throwable.class.isAssignableFrom(target.getClass())) {
			return throwableAdapter.adapt((Throwable) target);
		}
		return objectAdapter.adapt(target);
	}

	protected abstract String adapt(T target);

}
