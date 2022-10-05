package org.coody.framework.logged.config;

import org.coody.framework.logged.constant.LevelConstant;

public class LoggedConfig {

	/**
	 * 日志编码，默认UTF-8
	 */
	public static String encode = "UTF-8";

	/**
	 * 打印级别，默认DEBUG
	 */
	public static String level = LevelConstant.DEBUG;

	/**
	 * 是否输出在控制台，默认false
	 */
	public static boolean sysout = false;

	/**
	 * debug输出日志文件
	 */
	public static String outOfDebug = "debug.log";

	/**
	 * info输出日志文件
	 */
	public static String outOfInfo = "info.log";

	/**
	 * error输出日志文件
	 */
	public static String outOfError = "error.log";

}
