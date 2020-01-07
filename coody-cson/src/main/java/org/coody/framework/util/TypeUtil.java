package org.coody.framework.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.entity.TypeEntity;

public class TypeUtil {

	/**
	 * 缓存容器
	 */
	public static Map<Type, TypeEntity> TYPEENTITY_CACHE = new ConcurrentHashMap<Type, TypeEntity>();
	public static Map<Class<?>, TypeEntity> CLASS_TYPEENTITY_CACHE = new ConcurrentHashMap<Class<?>, TypeEntity>();
	/**
	 * 对象指纹
	 */
	public static final String MODEL_FINGERPRINT = "MODEL_FINGERPRINT";

	public static TypeEntity getTypeEntityByType(Type type) {
		TypeEntity typeEntity = TYPEENTITY_CACHE.get(type);
		if (typeEntity != null) {
			return typeEntity;
		}
		try {
			typeEntity = new TypeEntity();
			if (!(type instanceof ParameterizedType)) {
				typeEntity.setCurrent((Class<?>) type);
				return typeEntity;
			}
			Class<?> currentClazz = (Class<?>) ((ParameterizedType) type).getRawType();
			typeEntity.setCurrent(currentClazz);
			Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
			if (GeneralUtil.isNullOrEmpty(actualTypes)) {
				return typeEntity;
			}
			List<TypeEntity> actuals = new ArrayList<TypeEntity>(actualTypes.length);
			for (Type actualType : actualTypes) {
				TypeEntity actualTypeEntity = getTypeEntityByType(actualType);
				actuals.add(actualTypeEntity);
			}
			typeEntity.setActuals(actuals);
			return typeEntity;
		} finally {
			if (typeEntity == null) {
				typeEntity = new TypeEntity();
			}
			TYPEENTITY_CACHE.put(type, typeEntity);
		}
	}

	public static void main(String[] args) {
	}

}
