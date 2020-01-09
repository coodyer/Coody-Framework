package org.coody.framework.parser;

import java.util.Collection;

import org.coody.framework.convert.ValueConvert;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.parser.iface.AbstractParser;
import org.coody.framework.util.FieldUtil;

@SuppressWarnings("unchecked")
public class CollectionParser extends AbstractParser {

	@Override
	public <T> ObjectWrapper<T> doParser(String json, TypeEntity type) {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		if (output != ']') {
			return null;
		}
		StringBuilder sbBuilder = new StringBuilder();
		boolean inContent = false;

		boolean isString = true;

		char lastChr = '0';

		Collection<Object> object = type.newInstance();

		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();

		for (int i = 1; i < json.length(); i++) {
			wrapper.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				if(lastChr!='\\') {
					inContent = inContent ? false : true;
					isString = true;
					continue;
				}
			}
			if (!inContent) {
				if (chr == '[' || chr == '{') {
					ObjectWrapper<T> childWrapper = parser(json.substring(i, json.length()), type.getActuals().get(0));
					if (childWrapper != null) {
						object.add(childWrapper.getObject());
					}
					i += childWrapper.getOffset();
					sbBuilder = null;
					continue;
				}
				// 出栈
				if (chr == output) {
					if (sbBuilder != null) {
						// 完成解析
						object.add(FieldUtil.parseValue(ValueConvert.convert(sbBuilder.toString(), isString),
								type.getCurrent()));
						sbBuilder = null;
					}
					isString = false;
					break;
				}
				if (chr == ',') {
					if (sbBuilder != null) {
						object.add(FieldUtil.parseValue(ValueConvert.convert(sbBuilder.toString(), isString),
								type.getCurrent()));
						sbBuilder = null;
					}
					isString = false;
					continue;
				}
				if (chr == ':') {
					sbBuilder = null;
					isString = false;
					continue;
				}
			}
			if (sbBuilder == null) {
				sbBuilder = new StringBuilder();
			}
			lastChr = chr;
			// 读取内容
			sbBuilder.append(chr);
		}
		if (!object.isEmpty()) {
			wrapper.setObject((T) object);
		}
		return wrapper;
	}

}
