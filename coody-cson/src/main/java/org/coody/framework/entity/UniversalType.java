package org.coody.framework.entity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class UniversalType<T> {

	private final Type type;

	protected UniversalType() {
		Type superclass = this.getClass().getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("缺少类型参数");
		}
		ParameterizedType parameterizedType = (ParameterizedType) superclass;
		this.type = parameterizedType.getActualTypeArguments()[0];
	}

	public Type getType() {
		return type;
	}

}
