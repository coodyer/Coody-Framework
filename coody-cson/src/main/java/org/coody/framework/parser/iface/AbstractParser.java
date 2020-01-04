package org.coody.framework.parser.iface;

public abstract class AbstractParser<T> {

	public void parse(String json, Class<?> clazz) {

		json = json.trim();
		json = json.substring(1, json.length() - 1);
		char[] chars = json.toCharArray();

		StringBuilder content = null;

		Character input = null;

		for (int i = 0; i < chars.length; i++) {
			if (input == null) {
				if (chars[i] == '{' || chars[i] == '[' || chars[i] == '\"') {
					input = chars[i];
					content = new StringBuilder();
				}
				continue;
			}
			if (input == '\"' && chars[i] == '\"') {
				input = null;
				continue;
			}
			if (input == '[' && chars[i] == ']') {
				input = null;
				continue;
			}
			if (input == '{' && chars[i] == '}') {
				input = null;
				continue;
			}
			content.append(chars[i]);
		}
	}
}
