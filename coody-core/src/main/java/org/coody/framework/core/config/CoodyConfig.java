package org.coody.framework.core.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;

import org.coody.framework.core.build.ConfigBuilder;
import org.coody.framework.core.util.StringUtil;

public class CoodyConfig {

	/**
	 * 配置前缀
	 */
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
	public static final String CLASS_NAME="class";
	/**
	 * Bean配置
	 */
	public static final String BEAN_MAPPER = PREFIX + "\\.bean\\.${" + BEAN_NAME + "}\\.${" + PROPERTY + "}";
	/**
	 * Bean表达式
	 */
	public static final String INPUT_BEAN_MAPPER = "\\$\\{([A-Za-z0-9_]+)\\}";
	/**
	 * 扫描的包配置
	 */
	public String packager = "org.coody.framework";

	/**
	 * 要启动的组件
	 */
	public String assember = "";

	public void init() throws IllegalArgumentException, IllegalAccessException, IOException, URISyntaxException {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			String configField = PREFIX + "." + field.getName();
			String configValue = ConfigBuilder.getProperty(configField);
			if (StringUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			field.setAccessible(true);
			String defaulltValue = (String) field.get(this);
			if (StringUtil.isNullOrEmpty(defaulltValue)) {
				field.set(this, configValue);
				continue;
			}
			configValue = defaulltValue + "," + configValue;
			field.set(this, configValue);
		}

	}
}
