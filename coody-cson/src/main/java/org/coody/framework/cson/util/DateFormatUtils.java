package org.coody.framework.cson.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期类型与字符串类型相互转换
 */
public class DateFormatUtils {

	/**
	 * 时间转字符串
	 * 
	 * @param date   时间
	 * @param format 格式
	 * @return 结果
	 */
	public static String format(Date date, String format) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sfDate = new SimpleDateFormat(format);
		return sfDate.format(date);
	}

}
