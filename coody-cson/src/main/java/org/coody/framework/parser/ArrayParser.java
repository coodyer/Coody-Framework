package org.coody.framework.parser;

import java.lang.reflect.Array;
import java.util.Collection;

import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.parser.iface.AbstractParser;
import org.coody.framework.util.FieldUtil;

@SuppressWarnings("unchecked")
public class ArrayParser extends AbstractParser {

	@Override
	public <T> ObjectWrapper<T> doParser(String json, TypeEntity type) {
		ObjectWrapper<T> wrapper = collectionParser.doParser(json, type);
		if (wrapper.getObject() == null) {
			return wrapper;
		}
		wrapper.setObject((T) collectionToArray((Collection<?>) wrapper.getObject(), type.getCurrent()));
		return wrapper;
	}

	private static Object[] collectionToArray(Collection<?> collection, Class<?> type) {
		Object[] args = (Object[]) Array.newInstance(type.getComponentType(), collection.size());
		int index = 0;
		for (Object line : collection) {
			args[index] = FieldUtil.parseValue(line, type);
			index++;
		}
		return args;
	}

}
