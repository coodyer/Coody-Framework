package org.coody.framework.adapter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.coody.framework.exception.UnknownTypeException;

public class TypeAdapter<T> {

	private final Type type;

	public TypeAdapter() {
		Type superClazz = this.getClass().getGenericSuperclass();
		if (superClazz instanceof Class) {
			throw new UnknownTypeException("缺少类型参数");
		}
		ParameterizedType parameterizedType = (ParameterizedType) superClazz;
		this.type = parameterizedType.getActualTypeArguments()[0];
	}

	public Type getType() {
		return type;
	}

}
