package org.coody.framework.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.coody.framework.core.exception.UnsafeException;

import sun.misc.Unsafe;

/**
 * 为反射进行进一步加速
 * 
 * 19-03-07 本类慎用，已翻车多次
 * 
 * 19-03-28 重整本类，恢复使用。
 * 
 * 19-04-03 再次翻车
 * 
 * @author Coody
 *
 */
@SuppressWarnings({ "unchecked", "restriction" })
public class UnsafeUtil {

	private static Unsafe unsafe;

	static {
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (Unsafe) field.get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Long getFieldOffset(Field field) {
		Long fieldOffset = 0L;
		try {
			if (Modifier.isStatic(field.getModifiers())) {
				fieldOffset = unsafe.staticFieldOffset(field);
			} else {
				fieldOffset = unsafe.objectFieldOffset(field);
			}
			if (fieldOffset == null || fieldOffset == 0) {
				throw new UnsafeException("Unsafe获取字段内存地址异常，错误的内存地址>>" + field.getName() + ":" + fieldOffset);
			}
		} catch (Exception e) {
			return null;
		}
		return fieldOffset;
	}

	public static <T> T createInstance(Class<?> clazz) throws InstantiationException {
		return (T) unsafe.allocateInstance(clazz);
	}

	public static void setFieldValue(Object target, Field field, Object value) {
		Long fieldOffset = getFieldOffset(field);
		setFieldValue(target, fieldOffset, value);
	}

	public static void setFieldValue(Object target, Long fieldOffset, Object value) {
		if (fieldOffset == null || fieldOffset == 0) {
			throw new UnsafeException("Unsafe设置字段值异常，错误的内存地址>>" + fieldOffset);
		}
		unsafe.putObject(target, fieldOffset, value);
	}

	public static <T> T getFieldValue(Object target, Long fieldOffset) {
		if (fieldOffset == null || fieldOffset == 0) {
			throw new UnsafeException("Unsafe获取字段值异常，错误的内存地址>>" + fieldOffset);
		}
		return (T) unsafe.getObject(target, fieldOffset);
	}

}
