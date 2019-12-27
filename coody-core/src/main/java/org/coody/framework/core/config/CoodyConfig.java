package org.coody.framework.core.config;

import org.coody.framework.core.model.BaseConfig;

public class CoodyConfig extends BaseConfig {

	public static final String PREFIX = "coody";
	/**
	 * BeanName
	 */
	public static final String BEAN_NAME = "bean";

	/**
	 * Property
	 */
	public static final String PROPERTY = "property";
	/**
	 * ClassName
	 */
	public static final String CLASS_NAME = "class";
	/**
	 * Bean基础配置
	 */
	public static final String BEAN_CONFIG_MAPPER = PREFIX + "\\.bean\\.${" + BEAN_NAME + "}\\.${" + PROPERTY + "}";
	/**
	 * Bean构造参数配置
	 */
	public static final String BEAN_PARAMENT_MAPPER = PREFIX + "\\.bean\\.${" + BEAN_NAME + "}\\.parament\\.${"
			+ PROPERTY + "}";
	/**
	 * Bean字段配置
	 */
	public static final String BEAN_FIELD_MAPPER = PREFIX + "\\.bean\\.${" + BEAN_NAME + "}\\.field\\.${" + PROPERTY
			+ "}";
	/**
	 * Bean表达式
	 */
	public static final String INPUT_BEAN_MAPPER = "\\$\\{([A-Za-z0-9_]+)\\}";
	/**
	 * 扫描的包配置
	 */
	public String packager = "";

	/**
	 * 要启动的组件
	 */
	public String assember = "";

}
