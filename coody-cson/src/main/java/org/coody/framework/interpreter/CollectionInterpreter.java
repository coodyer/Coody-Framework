package org.coody.framework.interpreter;

import java.util.Collection;

import org.coody.framework.convert.ValueConvert;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.exception.BadFormatException;
import org.coody.framework.interpreter.iface.AbstractInterpreter;
import org.coody.framework.util.FieldUtil;

public class CollectionInterpreter extends AbstractInterpreter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> ObjectWrapper<T> doInterpreter(String json, TypeEntity type, int offset) {
		OutputSymbolWrapper output = getOutputSymbol(json, offset);
		if (output == null) {
			throw new BadFormatException("错误的Json格式");
		}

		StringBuilder sbBuilder = new StringBuilder();
		boolean inContent = false;

		boolean isString = true;

		char last = '0';

		int length = 0;

		Collection<Object> object = type.newInstance();

		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();

		for (int i = offset + output.getLength(); i < json.length(); i++) {
			length++;
			wrapper.setLength(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				if (last != '\\') {
					inContent = inContent ? false : true;
					continue;
				}
			}
			if (!inContent) {
				if (chr == '[' || chr == '{') {
					ObjectWrapper<T> childWrapper = interpreter(json, type.getActuals().get(0), i + 1);
					if (childWrapper != null) {
						object.add(childWrapper.getObject());
					}
					i += childWrapper.getLength();
					length += childWrapper.getLength();
					sbBuilder = null;
					continue;
				}
				// 出栈
				if (chr == output.getOutputSymbol()) {
					if (sbBuilder != null && sbBuilder.length() > 0) {
						// 完成解析
						object.add(FieldUtil.parseValue(ValueConvert.convert(sbBuilder.toString(), isString),
								type.getCurrent()));
						sbBuilder = null;
					}
					isString = false;
					break;
				}
				if (chr == ',') {
					if (sbBuilder != null && sbBuilder.length() > 0) {
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
			last = chr;
			if (!isString && chr == ' ') {
				continue;
			}
			// 读取内容
			sbBuilder.append(chr);
		}
		wrapper.setObject((T) object);
		wrapper.setLength(length);
		return wrapper;
	}

}
