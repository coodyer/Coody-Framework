package org.coody.framework.core.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

	public static void builder(String... dirs) throws IOException, URISyntaxException {
		for (String dir : dirs) {
			Enumeration<URL> urls = ConfigBuilder.class.getClassLoader().getResources(dir);
			if (StringUtil.isNullOrEmpty(urls)) {
				return;
			}
			while (urls.hasMoreElements()) {
				URL url = (URL) urls.nextElement();
				File file = new File(url.toURI());
				loadPropertByDir(file);
			}
		}
	}

	private static void loadPropertByDir(File file) throws URISyntaxException, IOException {
		if (!file.isDirectory()) {
			FileInputStream inStream = new FileInputStream(file);
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
		String[] files = file.list();
		if (StringUtil.isNullOrEmpty(files)) {
			return;
		}
		for (String filePath : files) {
			String path = file.getPath() + File.separator + filePath;
			File childFile = new File(path);
			loadPropertByDir(childFile);
		}
	}
}
