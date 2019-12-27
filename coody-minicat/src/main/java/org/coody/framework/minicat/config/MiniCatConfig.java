package org.coody.framework.minicat.config;

import org.coody.framework.core.model.BaseConfig;
import org.coody.framework.minicat.socket.BioService;

public class MiniCatConfig extends BaseConfig{
	
	
	public static final String PREFIX = "coody.minicat";
	/**
	 * Session超时时间
	 */
	public static Integer sessionTimeout = 60 * 1000 * 10;

	/**
	 * MiniCat HTTP线程数量
	 */
	public static Integer httpThread = 500;

	/**
	 * MiniCat 内务线程数量
	 */
	public static Integer minicatThread = 20;

	/**
	 * MiniCat端口
	 */
	public static Integer port = 80;

	/**
	 * HttpSocket超时时间
	 */
	public static Integer socketTimeout = 3000;

	/**
	 * HTTP SessionId字段名
	 */
	public static String sessionIdField = "COODYSESSID";

	/**
	 * 全局编码
	 */
	public static String encode = "UTF-8";

	/**
	 * 打开Gzip
	 */
	public static boolean openGzip = true;

	/**
	 * 模式 1Bio 2Nio
	 */
	public static String engine = BioService.class.getName();

	/**
	 * 最大Head长度
	 */
	public static Integer maxHeaderLength = 8192;
	/**
	 * 首页
	 */
	public static String WELCOME_PATH = "/index.do";
}
