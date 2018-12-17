package org.coody.framework.core.config;

import java.util.ArrayList;
import java.util.List;

public class CoodyConfig {

	/**
	 * 要扫描的包
	 */
	public static final List<String> SCANNER_PACKET=new ArrayList<String>();
	
	/**
	 * 要附加的类
	 */
	public static final List<Class<?>> INIT_CLAZZS=new ArrayList<Class<?>>();
	
	
	
	/**
	 * 扫描的包配置
	 */
	public static String base_package="";
	
	/**
	 * 要初始化的bean
	 */
	public static String addition="";
	
	
}
