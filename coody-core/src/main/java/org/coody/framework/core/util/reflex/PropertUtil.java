package org.coody.framework.core.util.reflex;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.exception.base.CoodyException;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.date.DateUtils;
import org.coody.framework.core.util.unsafe.UnsafeUtil;

@SuppressWarnings("unchecked")
public class PropertUtil {

	private static final Map<Class<?>, List<Field>> FIELD_MAP = new ConcurrentHashMap<Class<?>, List<Field>>();
	private static final Map<Class<?>, List<Method>> METHOD_MAP = new ConcurrentHashMap<Class<?>, List<Method>>();
	private static final Map<Method, List<FieldEntity>> PARAM_MAP = new ConcurrentHashMap<Method, List<FieldEntity>>();
	private static final Map<Method, Set<Method>> IFACE_METHODS = new ConcurrentHashMap<Method, Set<Method>>();
	private static final Map<Method, Set<Class<?>>> IFACE_CLAZZS = new ConcurrentHashMap<Method, Set<Class<?>>>();
	private static final Map<Class<?>, Set<Method>> CLAZZS_METHODS = new ConcurrentHashMap<Class<?>, Set<Method>>();
	private static final Map<String, Annotation> ANNOTATION_MAP = new ConcurrentHashMap<String, Annotation>();

	public static void reload() {
		FIELD_MAP.clear();
		METHOD_MAP.clear();
	}

	/**
	 * 获取对象多个字段的值
	 * 
	 * @param obj
	 * @param fieldNames
	 * @return
	 */
	public static List<Object> getFieldValues(Object obj, String... fieldNames) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		List<Object> values = new ArrayList<Object>(fieldNames.length * 2);
		for (String fieldName : fieldNames) {
			values.add(getFieldValue(obj, fieldName));
		}
		if (CommonUtil.isNullOrEmpty(values)) {
			return null;
		}
		return values;
	}

	/**
	 * 从对象中获取目标方法
	 * 
	 * @param methods    方法数组
	 * @param methodName 方法名称
	 * @param paras      参数列表
	 * @return
	 */
	public static Method getTargeMethod(Method[] methods, String methodName, Class<?>... paraTypes) {
		for (Method m : methods) {
			if (isTargeMethod(m, methodName, paraTypes)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * 判断目标是否是当前方法
	 * 
	 * @param method     当前方法
	 * @param methodName 目标方法名
	 * @param paras      目标方法参数列表
	 * @return
	 */
	private static boolean isTargeMethod(Method method, String methodName, Class<?>... paraTypes) {
		if (!method.getName().equals(methodName)) {
			return false;
		}
		Class<?>[] clas = method.getParameterTypes();
		if (CommonUtil.isNullOrEmpty(clas) && CommonUtil.isNullOrEmpty(paraTypes)) {
			return true;
		}
		if (CommonUtil.isNullOrEmpty(clas) || CommonUtil.isNullOrEmpty(paraTypes)) {
			return false;
		}
		if (clas.length != paraTypes.length) {
			return false;
		}
		for (int i = 0; i < clas.length; i++) {
			if (paraTypes[i] == null) {
				continue;
			}
			if (!clas[i].isAssignableFrom(paraTypes[i])) {
				return false;
			}
		}
		return true;
	}

	public static <T> T copyPropertys(Object source, Object targe) {
		if (CommonUtil.isNullOrEmpty(source)) {
			return (T) targe;
		}
		List<FieldEntity> entitys = getBeanFields(source);
		if (CommonUtil.isNullOrEmpty(entitys)) {
			return (T) targe;
		}
		for (FieldEntity entity : entitys) {
			try {
				setFieldValue(targe, entity.getFieldName(), entity.getFieldValue());
			} catch (Exception e) {
			}
		}
		return (T) targe;
	}

	/**
	 * 对象相同字段组成新list
	 * 
	 * @param list
	 * @param cla
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> List<T> getNewList(List list, Class cla) {
		if (CommonUtil.hasNullOrEmpty(list, cla)) {
			return null;
		}
		List<T> ls = new ArrayList<T>();
		for (Object obj : list) {
			try {
				Object newObj = UnsafeUtil.createInstance(cla);
				newObj = copyPropertys(obj, newObj);
				ls.add((T) newObj);
			} catch (Exception e) {
			}
		}
		return ls;
	}

	/**
	 * Map转对象
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> T mapToModel(Map map, Class<?> clazz) {
		if (CommonUtil.isNullOrEmpty(map)) {
			return null;
		}
		try {
			T value = (T) UnsafeUtil.createInstance(clazz);
			List<FieldEntity> entitys = getBeanFields(clazz);
			if (CommonUtil.isNullOrEmpty(entitys)) {
				return null;
			}
			for (FieldEntity entity : entitys) {
				try {
					entity.getSourceField().setAccessible(true);
					entity.getSourceField().set(value,
							parseValue(map.get(entity.getFieldName()), entity.getFieldType()));
				} catch (Exception e) {
				}
			}
			return value;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取某个对象的class
	 * 
	 * @param obj
	 * @return
	 */
	public static Class<? extends Object> getObjClass(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		if (obj.getClass().getName().equals(Class.class.getName())) {
			return (Class<?>) obj;
		}
		return obj.getClass();
	}

	/**
	 * 获取class的字段对象
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		List<Field> fields = loadFields(clazz);
		if (CommonUtil.isNullOrEmpty(fields)) {
			return null;
		}
		for (Field f : fields) {
			if (f.getName().equals(fieldName)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * 一个神奇的方法：获取对象字段集合
	 * 
	 * @param obj
	 * @return
	 */
	public static List<FieldEntity> getBeanFields(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		Class<? extends Object> cla = getObjClass(obj);
		List<FieldEntity> fields = getClassFields(cla);
		if (CommonUtil.isNullOrEmpty(fields)) {
			return fields;
		}
		if (obj.getClass().getName().equals(Class.class.getName())) {
			return fields;
		}
		for (FieldEntity field : fields) {
			try {
				Object value = field.getSourceField().get(obj);
				field.setFieldValue(value);
			} catch (Exception e) {

			}
		}
		return fields;
	}

	/**
	 * 一个神奇的方法：获取class字段集合
	 * 
	 * @param cla
	 * @return
	 */
	public static List<FieldEntity> getClassFields(Class<?> cla) {
		try {
			List<Field> fields = loadFields(cla);
			List<FieldEntity> infos = new ArrayList<FieldEntity>();
			for (Field f : fields) {
				if (f.getName().equalsIgnoreCase("serialVersionUID")) {
					continue;
				}
				if (f.getName().equalsIgnoreCase("$jacocodata")) {
					continue;
				}
				f.setAccessible(true);
				FieldEntity tmp = new FieldEntity();
				tmp.setFieldOffset(UnsafeUtil.getFieldOffset(f));
				tmp.setSourceField(f);
				tmp.setFieldAnnotations(f.getAnnotations());
				tmp.setFieldName(f.getName());
				tmp.setFieldType(f.getType());
				infos.add(tmp);
			}
			return infos;
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * 一个神奇的方法：从一个List提取字段名统一的分组
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> Collection<T> getGroup(List<?> objs, String fieldName, Object fieldValue) {
		if (CommonUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, Collection> map = PropertUtil.listToMaps(objs, fieldName);
		if (CommonUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map.get(fieldValue);
	}

	/**
	 * 从一个集合获取某指定字段值第一个对象
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> T getByList(List<?> objs, String fieldName, Object fieldValue) {
		if (CommonUtil.hasNullOrEmpty(objs, fieldName, fieldValue)) {
			return null;
		}
		Map map = PropertUtil.listToMap(objs, fieldName);
		if (CommonUtil.isNullOrEmpty(map)) {
			return null;
		}
		return (T) map.get(fieldValue);
	}

	/**
	 * 获取对象某个字段值
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	private static <T> T getFieldValueCurr(Object obj, String fieldName) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		Field f = getField(obj.getClass(), fieldName);
		if (CommonUtil.isNullOrEmpty(f)) {
			return null;
		}
		f.setAccessible(true);
		try {
			return (T) f.get(obj);
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * 获取注解字段值
	 * 
	 * @param bean
	 * @param paraName
	 * @return
	 */
	public static <T> T getAnnotationValue(Annotation annotation, String paraName) {
		if (CommonUtil.hasNullOrEmpty(annotation, paraName)) {
			return null;
		}
		try {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
			Field hField = invocationHandler.getClass().getDeclaredField("memberValues");
			hField.setAccessible(true);
			Map<?, ?> paraMap = (Map<?, ?>) hField.get(invocationHandler);
			return (T) paraMap.get(paraName);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 获取字段值，支持点属性
	 * 
	 * @param bean
	 * @param paraName
	 * @return
	 */
	public static <T> T getFieldValue(Object bean, String paraName) {
		if (CommonUtil.isNullOrEmpty(bean)) {
			return null;
		}
		List<FieldEntity> beanEntitys = PropertUtil.getBeanFields(bean);
		if (CommonUtil.isNullOrEmpty(beanEntitys)) {
			return null;
		}
		if (!paraName.contains(".")) {
			return PropertUtil.getFieldValueCurr(bean, paraName);
		}
		List<String> fields = new ArrayList<String>(Arrays.asList(paraName.split("\\.")));
		Object beanTmp = PropertUtil.getFieldValue(bean, fields.get(0));
		fields.remove(0);
		return getFieldValue(beanTmp, CommonUtil.splicing(fields, "."));
	}

	/**
	 * 获取方法的类
	 * 
	 * @param method
	 * @return
	 */
	public static Class<?> getClass(Executable executable) {
		Class<?> cla = (Class<?>) PropertUtil.getFieldValue(executable, "clazz");
		return cla;
	}

	/**
	 * 获取List对象某个字段的值组成新List
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static <T> List<T> getFieldValues(List<?> objs, String fieldName) {
		if (CommonUtil.isNullOrEmpty(objs)) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		Object value;
		for (Object obj : objs) {
			value = getFieldValue(obj, fieldName);
			list.add(value);
		}
		if (CommonUtil.isNullOrEmpty(objs)) {
			return null;
		}
		return (List<T>) list;
	}

	/**
	 * 获取对象字段列表
	 * 
	 * @param cla
	 * @return
	 */
	public static List<String> getFieldNames(Class<?> cla) {
		Field[] fields = cla.getDeclaredFields();
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj          实例对象
	 * @param propertyName 属性名
	 * @param value        新的字段值
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void setFieldValue(Object object, String propertyName, Object value) throws CoodyException {
		Field field = getField(object.getClass(), propertyName);
		if (CommonUtil.isNullOrEmpty(field)) {

			return;
		}
		setFieldValue(object, field, value);
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj          实例对象
	 * @param propertyName 属性名
	 * @param value        新的字段值
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void setFieldValue(Object object, Field field, Object value) throws CoodyException {
		field.setAccessible(true);
		if (field.getType().isEnum()) {
			try {
				setFieldValue(field, "name", value);
				Object enmValue = field.get(object);
				setFieldValue(enmValue, "name", value);
				return;
			} catch (Exception e) {
				throw new CoodyException("字段赋值失败", e);
			}
		}
		if (Modifier.isFinal(field.getModifiers())) {
			int modifiers = field.getModifiers();
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				try {
					modifiersField.setAccessible(true);
					modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
					Object obj = parseValue(value, field.getType());
					field.set(object, obj);
				} catch (IllegalAccessException e) {

					if (!CommonUtil.isNullOrEmpty(PropertUtil.getFieldValue(field, "fieldAccessor"))) {
						setProperties(field, "fieldAccessor.isReadOnly", false);
						setProperties(field, "fieldAccessor.isFinal", false);
						setProperties(field, "fieldAccessor.field", field);
					}
					if (!CommonUtil.isNullOrEmpty(PropertUtil.getFieldValue(field, "overrideFieldAccessor"))) {
						setProperties(field, "overrideFieldAccessor.isReadOnly", false);
						setProperties(field, "overrideFieldAccessor.isFinal", false);
						setProperties(field, "overrideFieldAccessor.field", field);
					}

					setFieldValue(field, "root", field);
					setFieldValue(object, field, value);
				} catch (Exception e) {

				} finally {
					if (modifiers != field.getModifiers()) {
						modifiersField.set(field, modifiers);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return;
		}
		Object obj = parseValue(value, field.getType());
		try {
			field.set(object, obj);
		} catch (Exception e) {
			throw new CoodyException("字段赋值失败", e);
		}
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj          实例对象
	 * @param propertyName 属性名
	 * @param value        新的字段值
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public static void setProperties(Object object, String propertyName, Object value)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		if (CommonUtil.isNullOrEmpty(object)) {
			return;
		}
		List<FieldEntity> beanEntitys = PropertUtil.getBeanFields(object);
		if (CommonUtil.isNullOrEmpty(beanEntitys)) {
			return;
		}
		if (!propertyName.contains(".")) {
			setFieldValue(object, propertyName, value);
			return;
		}
		List<String> fields = new ArrayList<String>(Arrays.asList(propertyName.split("\\.")));
		String fieldName = fields.get(0);
		FieldEntity currField = PropertUtil.getByList(beanEntitys, "fieldName", fieldName);
		if (currField == null || (currField.getFieldValue() == null && value == null)) {
			return;
		}
		Object beanTmp = currField.getFieldValue();
		if (beanTmp == null) {
			beanTmp = UnsafeUtil.createInstance(currField.getFieldType());
		}
		fields.remove(0);
		setProperties(beanTmp, CommonUtil.splicing(fields, "."), value);
		setProperties(object, fieldName, beanTmp);
	}

	/**
	 * 设置集合对象某字段值
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldsValue
	 * @return
	 */
	public static List<?> setFieldValues(List<?> objs, String fieldName, Object fieldsValue) {
		if (CommonUtil.isNullOrEmpty(objs)) {
			return null;
		}
		try {
			for (Object obj : objs) {
				try {
					if (CommonUtil.isNullOrEmpty(obj)) {
						continue;
					}
					setProperties(obj, fieldName, fieldsValue);
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {

		}
		return objs;
	}

	/**
	 * 一个神奇的方法：一个List根据某个字段排序
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> List<T> doSeq(Collection<?> objs, String fieldName) {
		if (CommonUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, Collection> maps = listToMaps(objs, fieldName);
		if (CommonUtil.isNullOrEmpty(maps)) {
			return null;
		}
		List list = new ArrayList();
		for (Object key : maps.keySet()) {
			try {
				list.addAll(maps.get(key));
			} catch (Exception e) {

			}
		}
		return list;
	}

	/**
	 * 一个神奇的方法：一个List根据某个字段排序
	 * 
	 * @param objs
	 * @param fieldName
	 * @param isDesc
	 * @return
	 */
	public static <T> List<T> doSeqDesc(List<?> objs, String fieldName) {
		List<T> list = doSeq(objs, fieldName);
		if (CommonUtil.isNullOrEmpty(list)) {
			return null;
		}
		Collections.reverse(list);
		return list;
	}

	/**
	 * 一个List转为Map，fieldName作为Key，所有字段值相同的组成List作为value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map<Object, Collection> listToMaps(Collection<?> objs, String fieldName) {
		if (CommonUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, Collection> map = new TreeMap<Object, Collection>();
		List<Object> list;
		for (Object obj : objs) {
			try {
				Object fieldValue = getFieldValue(obj, fieldName);
				if (map.containsKey(fieldValue)) {
					map.get(fieldValue).add(obj);
					continue;
				}
				list = new ArrayList<Object>();
				list.add(obj);
				map.put(fieldValue, list);
			} catch (Exception e) {

			}
		}
		if (CommonUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	/**
	 * List转为Map。fieldName作为Key，对象作为Value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		List<FieldEntity> entitys = PropertUtil.getBeanFields(obj);
		for (FieldEntity entity : entitys) {
			if (CommonUtil.isNullOrEmpty(entity.getFieldValue())) {
				continue;
			}
			map.put(entity.getFieldName(), entity.getFieldValue());
		}
		if (CommonUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	/**
	 * List转为Map。fieldName作为Key，对象作为Value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static Map<?, ?> listToMap(List<?> objs, String fieldName) {
		if (CommonUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, Object> map = new TreeMap<Object, Object>();
		for (Object obj : objs) {
			try {
				Object fieldValue = getFieldValue(obj, fieldName);
				map.put(fieldValue, obj);
			} catch (Exception e) {

			}
		}
		if (CommonUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	public static List<Method> loadMethods(Class<?> clazz) {
		List<Method> methods = METHOD_MAP.get(clazz);
		if (!CommonUtil.isNullOrEmpty(methods)) {
			return methods;
		}
		methods = new ArrayList<Method>(Arrays.<Method>asList(clazz.getDeclaredMethods()));
		if (!CommonUtil.isNullOrEmpty(clazz.getSuperclass())) {
			methods.addAll(loadMethods(clazz.getSuperclass()));
		}
		METHOD_MAP.put(clazz, methods);
		return methods;
	}

	/**
	 * 加载枚举的信息
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T loadEnumByField(Class<T> clazz, String fieldName, Object value) {
		if (!clazz.isEnum()) {
			throw new InvalidParameterException();
		}
		try {
			T[] enumConstants = clazz.getEnumConstants();
			for (T ec : enumConstants) {
				Object currValue = getFieldValue(ec, fieldName);
				if (value == currValue || currValue.equals(value)) {
					return ec;
				}
			}
			return null;
		} catch (Exception e) {

		}
		return null;
	}

	public static void setEnumFieldName(Class<?> clazz, String fieldName, String newFieldName) {
		if (!clazz.isEnum()) {
			throw new InvalidParameterException();
		}
		if (CommonUtil.hasNullOrEmpty(fieldName, newFieldName)) {
			return;
		}
		try {
			Object[] enumConstants = clazz.getEnumConstants();
			Field[] fields = clazz.getDeclaredFields();
			if (CommonUtil.isNullOrEmpty(fields)) {
				return;
			}
			List<Field> fieldList = new ArrayList<Field>();
			for (Field field : fields) {
				try {
					if (!(clazz.isAssignableFrom(field.getType()))
							&& !(("[L" + clazz.getName() + ";").equals(field.getType().getName()))) {
						fieldList.add(field);
					}
				} catch (Exception e) {
				}
			}
			if (CommonUtil.isNullOrEmpty(fieldList)) {
				return;
			}
			for (Object ec : enumConstants) {
				if (!ec.toString().equals(fieldName)) {
					continue;
				}
				setFieldValue(ec, "name", newFieldName);
			}
			return;
		} catch (Exception e) {

		}
		return;
	}

	public static void setEnumValue(Class<?> clazz, String fieldName, Map<String, Object> valueMaps) {
		if (!clazz.isEnum()) {
			throw new InvalidParameterException();
		}
		if (CommonUtil.isNullOrEmpty(valueMaps)) {
			return;
		}
		try {
			Object[] enumConstants = clazz.getEnumConstants();
			Field[] fields = clazz.getDeclaredFields();
			if (CommonUtil.isNullOrEmpty(fields)) {
				return;
			}
			List<Field> fieldList = new ArrayList<Field>();
			for (Field field : fields) {
				try {
					if (!(clazz.isAssignableFrom(field.getType()))
							&& !(("[L" + clazz.getName() + ";").equals(field.getType().getName()))) {
						fieldList.add(field);
					}
				} catch (Exception e) {
				}
			}
			if (CommonUtil.isNullOrEmpty(fieldList)) {
				return;
			}
			for (Object ec : enumConstants) {
				if (!ec.toString().equals(fieldName)) {
					continue;
				}
				for (Field field : fieldList) {
					for (String key : valueMaps.keySet()) {
						if (!key.equals(field.getName())) {
							continue;
						}
						setFieldValue(ec, field, valueMaps.get(key));
					}
				}
			}
			return;
		} catch (Exception e) {

		}
		return;
	}

	/**
	 * 获取class的字段列表
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Field> loadFields(Class<?> clazz) {
		List<Field> fields = FIELD_MAP.get(clazz);
		if (!CommonUtil.isNullOrEmpty(fields)) {
			return fields;
		}
		fields = new ArrayList<Field>();
		Field[] fieldArgs = clazz.getDeclaredFields();
		for (Field f : fieldArgs) {
			fields.add(f);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			fields.addAll(loadFields(superClass));
		}
		FIELD_MAP.put(clazz, fields);
		return fields;
	}

	/**
	 * 将对象某些字段置空
	 * 
	 * @param obj
	 * @param fieldNames
	 */
	public static void removeFields(Object obj, String... fieldNames) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return;
		}
		List<FieldEntity> fields = PropertUtil.getBeanFields(obj);
		Map<String, FieldEntity> map = (Map<String, FieldEntity>) listToMap(fields, "fieldName");
		for (String tmp : fieldNames) {
			try {
				if (map.containsKey(tmp)) {
					FieldEntity entity = map.get(tmp);
					PropertUtil.setProperties(obj, entity.getFieldName(), null);
				}
			} catch (Exception e) {

			}

		}
	}

	/**
	 * 清理其余字段，仅保留对象某些字段
	 * 
	 * @param obj
	 * @param fieldNames
	 */
	public static void accepFields(Object obj, String... fieldNames) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return;
		}
		List<FieldEntity> fields = PropertUtil.getBeanFields(obj);
		Map<String, FieldEntity> map = (Map<String, FieldEntity>) listToMap(fields, "fieldName");
		for (String tmp : fieldNames) {
			try {
				if (!map.containsKey(tmp)) {
					FieldEntity entity = map.get(tmp);
					PropertUtil.setProperties(obj, entity.getFieldName(), null);
				}
			} catch (Exception e) {

			}

		}
	}

	/**
	 * value值转换为对应的类型
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseValue(Object value, Class<?> clazz) {
		try {
			if (value == null) {
				if (clazz.isPrimitive()) {
					if (boolean.class.isAssignableFrom(clazz)) {
						return false;
					}
					if (byte.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (char.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (short.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (int.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (float.class.isAssignableFrom(clazz)) {
						return 0f;
					}
					if (long.class.isAssignableFrom(clazz)) {
						return 0L;
					}
					if (double.class.isAssignableFrom(clazz)) {
						return 0d;
					}
				}
				return value;
			}
			if (Class.class.isAssignableFrom(clazz)) {
				return Class.forName(value.toString());
			}
			if (clazz.isAssignableFrom(value.getClass())) {
				return value;
			}
			if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) {
				value = Integer.valueOf(value.toString());
				return value;
			}
			if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
				value = Float.valueOf(value.toString());
				return value;
			}
			if (Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
				value = Long.valueOf(value.toString());
				return value;
			}
			if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) {
				value = Double.valueOf(value.toString());
				return value;
			}
			if (Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz)) {
				value = Short.valueOf(value.toString());
				return value;
			}
			if (Byte.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz)) {
				value = Byte.valueOf(value.toString());
				return value;
			}
			if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
				value = ("true".equals(value.toString()) || "1".equals(value.toString())) ? true : false;
				return value;
			}
			if (String.class.isAssignableFrom(clazz)) {
				value = value.toString();
				return value;
			}
			if (Date.class.isAssignableFrom(clazz)) {
				value = DateUtils.toDate(value);
				return value;
			}
			return value;
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * 获取方法参数列表
	 * 
	 * @param method
	 * @return
	 */
	public static List<FieldEntity> getMethodParameters(Method method) {
		try {
			if (PARAM_MAP.containsKey(method)) {
				return PARAM_MAP.get(method);
			}
			Class<?>[] types = method.getParameterTypes();
			if (CommonUtil.isNullOrEmpty(types)) {
				return null;
			}
			List<String> paraNames = getMethodParaNames(method);
			if (CommonUtil.isNullOrEmpty(paraNames)) {
				return null;
			}
			Annotation[][] paraAnnotations = method.getParameterAnnotations();
			List<FieldEntity> entitys = new ArrayList<FieldEntity>();
			for (int i = 0; i < paraNames.size(); i++) {
				FieldEntity entity = new FieldEntity();
				entity.setFieldName(paraNames.get(i));
				entity.setFieldAnnotations(paraAnnotations[i]);
				entity.setFieldType(types[i]);
				entitys.add(entity);
			}
			PARAM_MAP.put(method, entitys);
			return entitys;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * 获取方法的参数名
	 * </p>
	 * 
	 * @param m
	 * @return
	 */
	public static List<String> getMethodParaNames(Method method) {
		return ParameterNameUtil.getParameters(method);
	}

	public static Set<Method> getIfaceMethods(Method method) {
		if (IFACE_METHODS.containsKey(method)) {
			return IFACE_METHODS.get(method);
		}
		Class<?> clazz = getClass(method);
		Set<Class<?>> infaceClazzs = getIfaceClass(clazz);
		if (CommonUtil.isNullOrEmpty(infaceClazzs)) {
			return null;
		}
		Set<Method> methods = new HashSet<Method>();
		for (Class<?> clazzTemp : infaceClazzs) {
			Method parentMethod = getTargeMethod(clazzTemp.getDeclaredMethods(), method.getName(),
					method.getParameterTypes());
			if (parentMethod == null) {
				continue;
			}
			methods.add(parentMethod);
		}
		return methods;
	}

	public static Set<Annotation> getDeclaredAnnotations(Method method) {
		Set<Method> methods = getIfaceMethods(method);
		if (methods == null) {
			methods = new HashSet<Method>();
		}
		methods.add(method);
		Set<Annotation> annotations = new HashSet<Annotation>();
		for (Method methodTemp : methods) {
			Annotation[] methodAnnotations = methodTemp.getAnnotations();
			if (CommonUtil.isNullOrEmpty(methodAnnotations)) {
				continue;
			}
			annotations.addAll(Arrays.asList(methodAnnotations));
		}
		return annotations;
	}

	@SuppressWarnings("unlikely-arg-type")
	public static Set<Class<?>> getIfaceClass(Class<?> clazz) {
		if (IFACE_CLAZZS.containsKey(clazz)) {
			return IFACE_CLAZZS.get(clazz);
		}
		Class<?>[] infaceClazzs = clazz.getInterfaces();
		if (CommonUtil.isNullOrEmpty(infaceClazzs)) {
			return null;
		}
		Set<Class<?>> infaceClazzList = new HashSet<Class<?>>(Arrays.asList(infaceClazzs));
		for (Class<?> clazzTemp : infaceClazzList) {
			Set<Class<?>> parentClazzs = getIfaceClass(clazzTemp);
			if (CommonUtil.isNullOrEmpty(parentClazzs)) {
				continue;
			}
			infaceClazzList.addAll(parentClazzs);
		}
		return infaceClazzList;
	}

	public static Set<Method> getMethods(Class<?> clazz) {
		if (CLAZZS_METHODS.containsKey(clazz)) {
			return CLAZZS_METHODS.get(clazz);
		}
		Set<Method> methods = new HashSet<Method>();
		Method[] clazzMethods = clazz.getDeclaredMethods();
		if (!CommonUtil.isNullOrEmpty(clazzMethods)) {
			methods.addAll(Arrays.asList(clazzMethods));
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null && superClass != Object.class) {
			Set<Method> superMethods = getMethods(superClass);
			if (!CommonUtil.isNullOrEmpty(superMethods)) {
				methods.addAll(superMethods);
			}
		}
		CLAZZS_METHODS.put(clazz, methods);
		return methods;
	}

	/**
	 * 设置注解字段值
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static void setAnnotationValue(Annotation annotation, String propertyName, Object value)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
		Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
		declaredField.setAccessible(true);
		Map<String, Object> memberValues = (Map<String, Object>) declaredField.get(invocationHandler);
		Object oldValue = memberValues.get(propertyName);
		if (oldValue != null) {
			value = PropertUtil.parseValue(value, oldValue.getClass());
		}
		memberValues.put(propertyName, value);
	}

	/**
	 * 设置注解字段值
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void setAnnotationValue(Annotation annotation, Map<String, Object> datas) {
		try {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
			Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
			declaredField.setAccessible(true);
			Map<String, Object> memberValues = (Map<String, Object>) declaredField.get(invocationHandler);
			if (CommonUtil.isNullOrEmpty(datas)) {
				memberValues.clear();
			}
			for (String key : datas.keySet()) {
				memberValues.put(key, datas.get(key));
			}
		} catch (Exception e) {
			throw new CoodyException("设置注解值失败", e);
		}

	}

	/**
	 * 获取注解字段map
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Map<String, Object> getAnnotationValueMap(Annotation annotation) {
		try {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
			Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
			declaredField.setAccessible(true);
			Map<String, Object> memberValues = (Map<String, Object>) declaredField.get(invocationHandler);
			return memberValues;
		} catch (Exception e) {
			throw new CoodyException("设置注解值失败", e);
		}
	}

	public static <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
		if (!annotationClass.isAnnotation()) {
			return null;
		}
		Annotation annotation = field.getAnnotation(annotationClass);
		if (annotation != null) {
			return (T) annotation;
		}
		annotation = ANNOTATION_MAP.get(field.getName() + annotationClass.getName());
		if (annotation != null) {
			return (T) annotation;
		}
		Annotation[] annotations = field.getAnnotations();
		if (CommonUtil.isNullOrEmpty(annotations)) {
			return null;
		}
		for (Annotation annotationTemp : annotations) {
			if (annotationTemp.annotationType().isAnnotationPresent(annotationClass)) {
				annotation = getAndPushValueParentAnnotation(annotationTemp, annotationClass);
				ANNOTATION_MAP.put(field.getName() + annotationClass.getName(), annotation);
				return (T) annotation;
			}
		}
		return null;
	}

	private synchronized static <T extends Annotation> T getParentAnnotation(Annotation annotation,
			Class<T> annotationClass) {
		if (annotation.annotationType() == annotationClass) {
			return (T) annotation;
		}
		Annotation[] annotationsForAnnotation = annotation.annotationType().getAnnotations();
		if (CommonUtil.isNullOrEmpty(annotationsForAnnotation)) {
			return null;
		}
		for (Annotation annotationForAnnotation : annotationsForAnnotation) {
			if (annotationClass == annotationForAnnotation.annotationType()) {
				return (T) annotationForAnnotation;
			}
			if (!annotationForAnnotation.annotationType().isAnnotationPresent(annotationClass)) {
				continue;
			}
			return getAndPushValueParentAnnotation(annotationForAnnotation, annotationClass);
		}
		return null;
	}

	private synchronized static <T extends Annotation> T getAndPushValueParentAnnotation(Annotation annotation,
			Class<T> annotationClass) {
		if (annotation.annotationType() == annotationClass) {
			return (T) annotation;
		}
		Annotation[] annotationsForAnnotation = annotation.annotationType().getAnnotations();
		if (CommonUtil.isNullOrEmpty(annotationsForAnnotation)) {
			return null;
		}
		for (Annotation annotationForAnnotation : annotationsForAnnotation) {
			if (annotationClass == annotationForAnnotation.annotationType()) {
				setAnnotationValue(annotationForAnnotation, getAnnotationValueMap(annotation));
				return (T) annotationForAnnotation;
			}
			if (!annotationForAnnotation.annotationType().isAnnotationPresent(annotationClass)) {
				continue;
			}
			setAnnotationValue(annotationForAnnotation, getAnnotationValueMap(annotation));
			return getAndPushValueParentAnnotation(annotationForAnnotation, annotationClass);
		}
		return null;
	}

	public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
		if (!annotationClass.isAnnotation()) {
			return null;
		}
		Annotation annotation = method.getAnnotation(annotationClass);
		if (annotation != null) {
			return (T) annotation;
		}
		annotation = ANNOTATION_MAP.get(method.getName() + annotationClass.getName());
		if (annotation != null) {
			return (T) annotation;
		}
		Annotation[] annotations = method.getAnnotations();
		if (CommonUtil.isNullOrEmpty(annotations)) {
			return null;
		}
		for (Annotation annotationTemp : annotations) {
			if (annotationTemp.annotationType().isAnnotationPresent(annotationClass)) {
				annotation = getAndPushValueParentAnnotation(annotationTemp, annotationClass);
				ANNOTATION_MAP.put(method.getName() + annotationClass.getName(), annotation);
				return (T) annotation;
			}
		}
		return null;
	}

	public static <T extends Annotation> List<T> getAnnotations(AccessibleObject accessible, Class<T> annotationClass) {
		if (!annotationClass.isAnnotation()) {
			return null;
		}
		Annotation[] annotations = accessible.getAnnotations();
		if (CommonUtil.isNullOrEmpty(annotations)) {
			return null;
		}
		List<Annotation> list = new ArrayList<Annotation>();
		for (Annotation annotationTemp : annotations) {
			if (annotationTemp.annotationType() != annotationClass
					&& !annotationTemp.annotationType().isAnnotationPresent(annotationClass)) {
				continue;
			}
			if (annotationTemp.annotationType() == annotationClass) {
				list.add((T) annotationTemp);
				continue;
			}
			Annotation parentAnnotation = getAndPushValueParentAnnotation(annotationTemp, annotationClass);
			if (parentAnnotation == null) {
				continue;
			}
			list.add((T) parentAnnotation);
		}
		if (CommonUtil.isNullOrEmpty(list)) {
			return null;
		}
		return (List<T>) list;
	}

	public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
		if (!annotationClass.isAnnotation()) {
			return null;
		}

		Annotation annotation = clazz.getAnnotation(annotationClass);
		if (annotation != null) {
			return (T) annotation;
		}
		annotation = ANNOTATION_MAP.get(clazz.getName() + annotationClass.getName());
		if (annotation != null) {
			return (T) annotation;
		}
		Annotation[] annotations = clazz.getAnnotations();
		if (CommonUtil.isNullOrEmpty(annotations)) {
			return null;
		}
		for (Annotation annotationTemp : annotations) {
			if (annotationTemp.annotationType().isAnnotationPresent(annotationClass)) {
				annotation = getAndPushValueParentAnnotation(annotationTemp, annotationClass);
				ANNOTATION_MAP.put(clazz.getName() + annotationClass.getName(), annotation);
				return (T) annotation;
			}
		}
		return null;
	}

	public static <T extends Annotation> List<Annotation> getAnnotations(Class<?> clazz, Class<T> annotationClass) {
		if (!annotationClass.isAnnotation()) {
			return null;
		}
		Annotation[] annotations = clazz.getAnnotations();
		if (CommonUtil.isNullOrEmpty(annotations)) {
			return null;
		}
		List<Annotation> list = new ArrayList<Annotation>();
		for (Annotation annotationTemp : annotations) {
			if (annotationTemp.annotationType() != annotationClass
					&& !annotationTemp.annotationType().isAnnotationPresent(annotationClass)) {
				continue;
			}
			Annotation parentAnnotation = getParentAnnotation(annotationTemp, annotationClass);
			if (parentAnnotation == null) {
				continue;
			}
			if (parentAnnotation.annotationType() == annotationClass) {
				list.add((T) annotationTemp);
				continue;
			}
			list.add((T) parentAnnotation);
		}
		if (CommonUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list;
	}

	/**
	 * 为方法或字段添加注解
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void addAnnotations(AccessibleObject accessible, Annotation... annotations) throws Exception {
		synchronized (accessible) {
			if (CommonUtil.hasNullOrEmpty(accessible, annotations)) {
				throw new CoodyException("accessible or annotations is empty");
			}
			accessible.getAnnotations();
			Field declaredAnnotationsField = PropertUtil.getField(accessible.getClass(), "declaredAnnotations");
			if (Modifier.isTransient(declaredAnnotationsField.getModifiers())) {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.set(declaredAnnotationsField,
						declaredAnnotationsField.getModifiers() & ~Modifier.TRANSIENT);
			}
			declaredAnnotationsField.setAccessible(true);
			Object declaredAnnotationsObject = declaredAnnotationsField.get(accessible);
			if (declaredAnnotationsObject == null || declaredAnnotationsObject == Collections.EMPTY_MAP) {
				declaredAnnotationsObject = new LinkedHashMap<Class<?>, Annotation>();
			}
			LinkedHashMap<Class<?>, Annotation> declaredAnnotations = (LinkedHashMap<Class<?>, Annotation>) declaredAnnotationsObject;
			for (Annotation annotation : annotations) {
				if (annotation == null) {
					continue;
				}
				if (declaredAnnotations.containsKey(annotation.annotationType())) {
					continue;
				}
				declaredAnnotations.put(annotation.annotationType(), annotation);
			}
			PropertUtil.setFieldValue(accessible, "declaredAnnotations", declaredAnnotations);
			AccessibleObject root = PropertUtil.getFieldValue(accessible, "root");
			while (root != null) {
				addAnnotations(root, annotations);
				root = PropertUtil.getFieldValue(root, "root");
			}
		}
	}

	/**
	 * 为方法或字段添加注解
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void addAnnotations(Class<?> clazz, Annotation... annotations) throws Exception {
		synchronized (clazz) {
			if (CommonUtil.hasNullOrEmpty(clazz, annotations)) {
				throw new CoodyException("accessible or annotations is empty");
			}
			clazz.getAnnotations();
			Field annotationDataField = PropertUtil.getField(clazz.getClass(), "annotationData");
			annotationDataField.setAccessible(true);
			Object annotationData = annotationDataField.get(clazz);
			Field declaredAnnotationsField = PropertUtil.getField(annotationData.getClass(), "declaredAnnotations");

			if (Modifier.isTransient(declaredAnnotationsField.getModifiers())) {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.set(declaredAnnotationsField,
						declaredAnnotationsField.getModifiers() & ~Modifier.TRANSIENT);
			}
			declaredAnnotationsField.setAccessible(true);
			Object declaredAnnotationsObject = declaredAnnotationsField.get(annotationData);
			if (declaredAnnotationsObject == null || declaredAnnotationsObject == Collections.EMPTY_MAP) {
				declaredAnnotationsObject = new LinkedHashMap<Class<?>, Annotation>();
			}
			LinkedHashMap<Class<?>, Annotation> declaredAnnotations = (LinkedHashMap<Class<?>, Annotation>) declaredAnnotationsObject;
			for (Annotation annotation : annotations) {
				if (annotation == null) {
					continue;
				}
				if (declaredAnnotations.containsKey(annotation.annotationType())) {
					continue;
				}
				declaredAnnotations.put(annotation.annotationType(), annotation);
			}
			PropertUtil.setFieldValue(annotationData, "declaredAnnotations", declaredAnnotations);
		}
	}
}