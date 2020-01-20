package org.coody.framework.cson.interpreter;

import java.util.Map;

import org.coody.framework.cson.convert.ValueConvert;
import org.coody.framework.cson.entity.ObjectWrapper;
import org.coody.framework.cson.entity.TypeEntity;
import org.coody.framework.cson.exception.BadFormatException;
import org.coody.framework.cson.interpreter.iface.AbstractInterpreter;

public class MapInterpreter extends AbstractInterpreter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> ObjectWrapper<T> doInterpreter(String json, TypeEntity type, int offset) {

		OutputSymbolWrapper output = getOutputSymbol(json, offset);
		if (output == null) {
			throw new BadFormatException("错误的Json格式");
		}

		StringBuilder temp = new StringBuilder();

		boolean inContent = false;

		boolean isString = false;

		char lastChr = '0';

		Object field = null;

		Map<Object, Object> map = type.newInstance();

		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();

		int length = 0;

		for (int i = offset + output.getLength(); i < json.length(); i++) {
			length++;
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
					ObjectWrapper<T> childWrapper = interpreter(json, type.getActuals().get(1), i);
					if (childWrapper.getObject() != null) {
						if (field == null) {
							field = childWrapper.getObject();
						} else {
							map.put(ValueConvert.convert(field, isString), childWrapper.getObject());
							field = null;
						}
					}
					i += childWrapper.getLength();
					length += childWrapper.getLength();
					continue;
				}
				if (chr == '{') {
					ObjectWrapper<T> childWrapper = interpreter(json, type.getActuals().get(1), i);
					if (childWrapper.getObject() != null) {
						if (field == null) {
							field = childWrapper.getObject();
						} else {
							map.put(field, childWrapper.getObject());
							field = null;
						}
					}
					i += childWrapper.getLength();
					length += childWrapper.getLength();
					continue;
				}
				// 出栈
				if (chr == output.getOutputSymbol()) {
					if (field != null) {
						map.put(field, ValueConvert.convert(temp, isString));
						field = null;
					}
					isString = false;
					break;
				}
				if (chr == ',') {
					if (field != null) {
						map.put(field, ValueConvert.convert(temp, isString));
						field = null;
					}
					isString = false;
					temp = new StringBuilder();
					continue;
				}
				if (chr == ':') {
					if (field == null) {
						field = ValueConvert.convert(temp, isString);
					}
					temp = new StringBuilder();
					isString = false;
					continue;
				}
			}
			lastChr = chr;
			if (!isString && chr == ' ') {
				continue;
			}
			// 读取内容
			temp.append(chr);

		}
		wrapper.setObject((T) map);
		wrapper.setLength(length);
		return wrapper;
	}

}
