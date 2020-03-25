package org.coody.framework.cson.interpreter;

import org.coody.framework.cson.entity.ObjectWrapper;
import org.coody.framework.cson.entity.TypeEntity;
import org.coody.framework.cson.exception.BadFormatException;
import org.coody.framework.cson.interpreter.iface.AbstractInterpreter;

public class StringInterpreter extends AbstractInterpreter {

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

		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();

		int length = 0;

		for (int i = offset + output.getLength()-1; i < json.length(); i++) {
			length++;
			char chr = json.charAt(i);
			temp.append(chr);
			if (chr == '"') {
				if (lastChr != '\\') {
					inContent = inContent ? false : true;
					isString = true;
					continue;
				}
			}
			if (!inContent) {
				// 出栈
				if (chr == output.getOutputSymbol()) {
					isString = false;
					break;
				}
			}
			lastChr = chr;
			if (!isString && chr == ' ') {
				continue;
			}
		}
		wrapper.setObject((T) temp.toString());
		wrapper.setLength(length);
		return wrapper;
	}

}
