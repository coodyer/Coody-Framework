package org.coody.framework.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

//import oracle.sql.CLOB;

public class CommonUtil {

	/**
	 * 生成指定数目字符串按分隔符分割
	 * 
	 * @return
	 */
	public static String createString(String element, String symbol, Integer size) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			if (isNullOrEmpty(element)) {
				continue;
			}
			list.add(element);
		}
		return splicing(list, symbol);
	}

	/**
	 * 根据分割符将字符串分割成String数组
	 * 
	 */
	public static String[] splitToStringArray(String src, String symbol) {
		Vector<String> splitArrays = new Vector<String>();
		int i = 0;
		int j = 0;
		while (i <= src.length()) {
			j = src.indexOf(symbol, i);
			if (j < 0) {
				j = src.length();
			}
			splitArrays.addElement(src.substring(i, j));
			i = j + 1;
		}
		int size = splitArrays.size();
		String[] array = new String[size];
		System.arraycopy(splitArrays.toArray(), 0, array, 0, size);
		return array;
	}

	/**
	 * 把一个或多个字符串按照分隔符拼接成字符串
	 * 
	 * @param 数组参数
	 * @param 分隔符
	 * @return
	 */
	public static String splicing(Object[] args, String symbol) {
		List<Object> objList = Arrays.asList(args);
		return splicing(objList, symbol);
	}

	public static String splicing(Set<?> set, String symbol) {
		return splicing(new ArrayList<Object>(set), symbol);
	}

	/**
	 * 把一个集合按照分隔符拼接成字符串
	 * 
	 * @param 集合参数
	 * @param 分隔符
	 * @return 字符串
	 */
	public static String splicing(Collection<?> list, String symbol) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Object obj : list) {
			if (isNullOrEmpty(obj)) {
				continue;
			}
			sb.append(obj);
			if (i < list.size() - 1) {
				sb.append(symbol);
			}
			i++;
		}
		return sb.toString();
	}

	public static boolean isNullOrEmpty(Object obj) {
		try {
			if (obj == null) {
				return true;
			}
			if (obj instanceof CharSequence) {
				return ((CharSequence) obj).length() == 0;
			}
			if (obj instanceof Collection) {
				return ((Collection<?>) obj).isEmpty();
			}
			if (obj instanceof Map) {
				return ((Map<?, ?>) obj).isEmpty();
			}
			if (obj instanceof Object[]) {
				Object[] object = (Object[]) obj;
				if (object.length == 0) {
					return true;
				}
				boolean empty = true;
				for (int i = 0; i < object.length; i++) {
					if (!isNullOrEmpty(object[i])) {
						empty = false;
						break;
					}
				}
				return empty;
			}
			return false;
		} catch (Exception e) {
			return true;
		}

	}

	public static boolean hasNullOrEmpty(Object... objs) {
		if (isNullOrEmpty(objs)) {
			return true;
		}
		for (int i = 0; i < objs.length; i++) {
			if (isNullOrEmpty(objs[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean allIsNullOrEmpty(Object... objs) {
		if (objs == null || objs.length == 0) {
			return true;
		}
		for (int i = 0; i < objs.length; i++) {
			if (!isNullOrEmpty(objs[i])) {
				return false;
			}
		}
		return true;
	}

	public static String formatPath(String path) {
		if (CommonUtil.isNullOrEmpty(path)) {
			return null;
		}
		path = path.replace("\\", "/");
		while (path.contains("//")) {
			path = path.replace("//", "/");
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return path;
	}

}
