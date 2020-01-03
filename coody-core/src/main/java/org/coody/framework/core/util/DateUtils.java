package org.coody.framework.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期类型与字符串类型相互转换
 */
public class DateUtils {

	public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String DATE_PATTERN = "yyyy-MM-dd";

	public static String getWeek(Date date) {
		final String dayTags[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek < 0) {
			dayOfWeek = 0;
		}
		return dayTags[dayOfWeek];
	}

	/**
	 * 获取当前日期
	 * 
	 * @return 当前日期
	 */
	public static String getDateString() {
		return toString(new Date(), DATE_PATTERN);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return 当前日期
	 */
	public static String getDateTimeString() {
		return toString(new Date(), DATETIME_PATTERN);
	}

	/**
	 * 时间增减
	 * 
	 * @param date   时间
	 * @param change 增减值
	 * @return 结果
	 */
	public static Date change(Date date, int calendarField, Integer change) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, change);
		return c.getTime();
	}

	/**
	 * 时间转字符串
	 * 
	 * @param date   时间
	 * @param format 格式
	 * @return 结果
	 */
	public static String toString(Date date, String format) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sfDate = new SimpleDateFormat(format);
		return sfDate.format(date);
	}

	public static String toString(Date date) {
		return toString(date, DATETIME_PATTERN);
	}

	public static Date toDate(Object value) {
		if (value == null) {
			return null;
		}
		try {
			Class<?> clazz = value.getClass();
			if (clazz.isPrimitive()) {
				if (value.toString().length() == 13) {
					return new Date(Long.valueOf(value.toString()));
				}
			}
			if (MatchUtil.isMatcher(value.toString(), "\\d{13}")) {
				value = new Date(Long.valueOf(value.toString()));
				return (Date) value;
			}
			if (MatchUtil.isMatcher(value.toString(), "\\d{8}")) {
				value = new SimpleDateFormat("yyyyMMdd").parse(value.toString());
				return (Date) value;
			}
			if (MatchUtil.isMatcher(value.toString(), "\\d{10}")) {
				value = new SimpleDateFormat("yyyyMMddHH").parse(value.toString());
				return (Date) value;
			}
			if (MatchUtil.isMatcher(value.toString(), "\\d{12}")) {
				value = new SimpleDateFormat("yyyyMMddHHmm").parse(value.toString());
				return (Date) value;
			}
			if (MatchUtil.isMatcher(value.toString(), "\\d{14}")) {
				value = new SimpleDateFormat("yyyyMMddHHmmss").parse(value.toString());
				return (Date) value;
			}
			if (MatchUtil.isMatcher(value.toString(), "\\d{17}")) {
				value = new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(value.toString());
				return (Date) value;
			}
			if (MatchUtil.isMatcher(value.toString(), "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) {
				value = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
				return (Date) value;
			}
			if (MatchUtil.isMatcher(value.toString(),
					"^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*")) {
				value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.toString());
				return (Date) value;
			}
			return (Date) value;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDayCode() {
		return toString(getDayBeginTime(), DATE_PATTERN);
	}

	public static Date getDayBeginTime(Date date) {
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(date);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}

	public static Date getDayBeginTime() {
		return getDayBeginTime(new Date());
	}

	public static Date getDayEndTime(Date date) {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.setTime(date);
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}

	public static String getWeekCode() {
		return toString(getWeekBeginTime(), DATE_PATTERN);
	}

	public static Date getWeekBeginTime() {
		return getWeekBeginTime(new Date());
	}

	public static Date getWeekBeginTime(Date date) {
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(date);
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return (Date) currentDate.getTime();
	}

	public static String getMonthCode() {
		return toString(getMonthBeginTime(), DATE_PATTERN);
	}

	public static Date getMonthBeginTime() {
		return getMonthBeginTime(new Date());
	}

	public static Date getMonthBeginTime(Date date) {
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(date);
		currentDate.set(Calendar.DAY_OF_MONTH, 1);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		return currentDate.getTime();
	}

}
