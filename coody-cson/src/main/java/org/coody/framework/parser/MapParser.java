package org.coody.framework.parser;

import java.util.Map;

import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.parser.iface.AbstractParser;

@SuppressWarnings("unchecked")
public class MapParser extends AbstractParser {

	@Override
	public <T> ObjectWrapper<T> doParser(String json, TypeEntity type) {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		StringBuilder sbBuilder = new StringBuilder();

		boolean inContent = false;

		String field = null;
		
		Map<Object, Object> map = type.newInstance();
		
		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();

		for (int i = 1; i < json.length(); i++) {
			wrapper.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				inContent = inContent ? false : true;
				continue;
			}
			if (!inContent) {
				if (chr == '[') {
					ObjectWrapper<T> childWrapper = parser(json.substring(i, json.length()), type.getActuals().get(1));
					if (childWrapper.getObject() != null) {
						map.put(field, childWrapper.getObject());
					}
					i += childWrapper.getOffset();
					field = null;
					continue;
				}
				if (chr == '{') {
					ObjectWrapper<T> childWrapper = parser(json.substring(i, json.length()), type.getActuals().get(1));
					if (childWrapper.getObject() != null) {
						map.put(field, childWrapper.getObject());
					}
					i += childWrapper.getOffset();
					field = null;
					continue;
				}
				// 出栈
				if (chr == output) {
					if (field != null) {
						map.put(field, sbBuilder.toString());
					}
					break;
				}
				if (chr == ',') {
					if (field != null) {
						map.put(field, sbBuilder.toString());
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
		if (!map.isEmpty()) {
			wrapper.setObject((T) map);
		}
		return wrapper;
	}

}
