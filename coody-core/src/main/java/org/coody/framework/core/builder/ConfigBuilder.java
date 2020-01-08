package org.coody.framework.core.builder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.coody.framework.core.exception.base.CoodyException;
import org.coody.framework.core.model.BaseConfig;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

public class ConfigBuilder {

	private static Map<String, String> config = new HashMap<String, String>();

	public static String getProperty(String key) {
		return config.get(key);
	}

	public static Set<String> propertyKeySet() {
		return config.keySet();
	}

	public static Integer getPropertyInteger(String key) {
		return StringUtil.toInteger(config.get(key));
	}

	public static void builder() throws IOException, URISyntaxException {
		if (!config.isEmpty()) {
			return;
		}
		InputStream ins = ConfigBuilder.class.getClassLoader().getResourceAsStream("coody.properties");
		if (ins == null) {
			throw new CoodyException("coody.properties不存在");
		}
		loadPropertByDir(ins);
	}

	private static void loadPropertByDir(InputStream inStream) throws URISyntaxException, IOException {
		Properties prop = new Properties();
		prop.load(inStream);
		Enumeration<Object> keys = prop.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = prop.getProperty(key);
			if (StringUtil.hasNull(key, value)) {
				value = "";
			}
			config.put(key, value.trim());
		}
		return;
	}

	@SuppressWarnings("unchecked")
	public static <T> T flush(BaseConfig config, String prefix)
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = config.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			String configField = prefix + "." + field.getName();
			Object configValue = ConfigBuilder.getProperty(configField);
			if (StringUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			configValue = PropertUtil.parseValue(configValue, field.getType());
			field.setAccessible(true);
			field.set(config, configValue);
			continue;
		}
		return (T) config;

	}
}
