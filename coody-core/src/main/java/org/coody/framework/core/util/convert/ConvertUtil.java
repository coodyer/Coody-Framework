package org.coody.framework.core.util.convert;

import org.coody.framework.core.util.CommonUtil;

public class ConvertUtil {


	public static Integer toInteger(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		try {
			return Integer.valueOf(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static String toString(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		try {
			return String.valueOf(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Double toDouble(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		try {
			return Double.valueOf(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Float toFloat(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		try {
			return Float.valueOf(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Long toLong(Object obj) {
		if (CommonUtil.isNullOrEmpty(obj)) {
			return null;
		}
		try {
			return Long.valueOf(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}
}
