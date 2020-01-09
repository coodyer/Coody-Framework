package org.coody.framework.parser;

import java.util.Map;

import org.coody.framework.convert.ValueConvert;
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

		boolean isString = true;

		char lastChr = '0';

		Object field = null;

		Map<Object, Object> map = type.newInstance();

		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();

		for (int i = 1; i < json.length(); i++) {
			wrapper.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				if (lastChr != '\\') {
					inContent = inContent ? false : true;
					isString = true;
					continue;
				}
			}
			if (!inContent) {
				if (chr == '[') {
					ObjectWrapper<T> childWrapper = parser(json.substring(i, json.length()), type.getActuals().get(1));
					if (childWrapper.getObject() != null) {
						if (field == null) {
							field = childWrapper.getObject();
						} else {
							map.put(ValueConvert.convert(field, isString), childWrapper.getObject());
							field = null;
						}
					}
					i += childWrapper.getOffset();
					continue;
				}
				if (chr == '{') {
					ObjectWrapper<T> childWrapper = parser(json.substring(i, json.length()), type.getActuals().get(1));
					if (childWrapper.getObject() != null) {
						if (field == null) {
							field = childWrapper.getObject();
						} else {
							map.put(ValueConvert.convert(field, isString), childWrapper.getObject());
							field = null;
						}
					}
					i += childWrapper.getOffset();
					continue;
				}
				// 出栈
				if (chr == output) {
					if (field != null) {
						map.put(field, ValueConvert.convert(sbBuilder, isString));
						field = null;
					}
					isString = false;
					break;
				}
				if (chr == ',') {
					if (field != null) {
						map.put(field, ValueConvert.convert(sbBuilder, isString));
						field = null;
					}
					isString = false;
					sbBuilder = new StringBuilder();
					continue;
				}
				if (chr == ':') {
					if (field == null) {
						field = ValueConvert.convert(sbBuilder, isString);
					}
					sbBuilder = new StringBuilder();
					isString = false;
					continue;
				}
			}
			lastChr = chr;
			// 读取内容
			sbBuilder.append(chr);
		}
		if (!map.isEmpty()) {
			wrapper.setObject((T) map);
		}
		return wrapper;
	}

}
