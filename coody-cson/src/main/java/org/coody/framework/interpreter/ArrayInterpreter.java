package org.coody.framework.interpreter;

import java.lang.reflect.Array;
import java.util.Collection;

import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.interpreter.iface.AbstractInterpreter;
import org.coody.framework.util.FieldUtil;

public class ArrayInterpreter extends AbstractInterpreter {

	CollectionInterpreter collectionInterpreter = new CollectionInterpreter();

	@SuppressWarnings("unchecked")
	@Override
	public <T> ObjectWrapper<T> doInterpreter(String json, TypeEntity type, int offset) {
		ObjectWrapper<T> wrapper = collectionInterpreter.doInterpreter(json, type, offset);
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
