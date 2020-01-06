package org.coody.framework.parser.iface;

import org.coody.framework.entity.CsonArray;
import org.coody.framework.entity.CsonObject;

import com.alibaba.fastjson.JSON;

public abstract class AbstractParser {
	public abstract void parseValue(String json, Object object);

	public static CsonArray parseCsonArray(String json) throws InstantiationException, IllegalAccessException {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		StringBuilder sbBuilder = new StringBuilder();

		boolean inContent = false;

		CsonArray cson = new CsonArray();
		for (int i = 1; i < json.length(); i++) {
			cson.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				inContent = inContent ? false : true;
				continue;
			}
			if (!inContent) {
				if (chr == '[') {
					CsonArray child = parseCsonArray(json.substring(i, json.length()));
					cson.add(child);
					i += child.getOffset();
					continue;
				}
				if (chr == '{') {
					CsonObject child = parseCson(json.substring(i, json.length()));
					cson.add(child);
					i += child.getOffset();
					continue;
				}
				// 出栈
				if (chr == output) {
					// 完成解析
					cson.add(sbBuilder);
					break;
				}
				if (chr == ',') {
					cson.add(sbBuilder);
					sbBuilder = new StringBuilder();
					continue;
				}
				if (chr == ':') {
					sbBuilder = new StringBuilder();
					continue;
				}
			}
			// 读取内容
			sbBuilder.append(chr);
		}
		return cson;
	}

	public static CsonObject parseCson(String json) throws InstantiationException, IllegalAccessException {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		StringBuilder sbBuilder = new StringBuilder();

		boolean inContent = false;

		String field = null;
		CsonObject cson = new CsonObject();
		for (int i = 1; i < json.length(); i++) {
			cson.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				inContent = inContent ? false : true;
				continue;
			}
			if (!inContent) {
				if (chr == '[') {
					CsonArray child = parseCsonArray(json.substring(i, json.length()));
					cson.put(field, child);
					i += child.getOffset();
					field = null;
					continue;
				}
				if (chr == '{') {
					CsonObject child = parseCson(json.substring(i, json.length()));
					cson.put(field, child);
					i += child.getOffset();
					field = null;
					continue;
				}
				// 出栈
				if (chr == output) {
					if (field != null) {
						cson.put(field, sbBuilder);
					}
					break;
				}
				if (chr == ',') {
					if (field != null) {
						cson.put(field, sbBuilder);
					}
					sbBuilder = new StringBuilder();
					continue;
				}
				if (chr == ':') {
					field = sbBuilder.toString();
					sbBuilder = new StringBuilder();
					continue;
				}
			}
			// 读取内容
			sbBuilder.append(chr);
		}
		return cson;
	}

	private static Character getOutputSymbol(char chr) {
		if (chr == '{') {
			return '}';
		}
		if (chr == '[') {
			return ']';
		}
		return null;
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		String json = "{user:Coody}";
		CsonObject object = AbstractParser.parseCson(json);
		System.out.println("得到结果:" + JSON.toJSONString(object));
	}

}
