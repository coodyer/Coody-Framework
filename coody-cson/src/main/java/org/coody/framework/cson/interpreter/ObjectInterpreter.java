package org.coody.framework.cson.interpreter;

import org.coody.framework.cson.convert.ValueConvert;
import org.coody.framework.cson.entity.JsonFieldEntity;
import org.coody.framework.cson.entity.ObjectWrapper;
import org.coody.framework.cson.entity.TypeEntity;
import org.coody.framework.cson.exception.BadFormatException;
import org.coody.framework.cson.interpreter.iface.AbstractInterpreter;
import org.coody.framework.cson.util.FieldUtil;
import org.coody.framework.cson.util.TypeUtil;

public class ObjectInterpreter extends AbstractInterpreter {

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

		char last = '0';
		
		int length = 0;

		String field = null;

		Object object = type.newInstance();

		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();


		for (int i = offset + output.getLength(); i < json.length(); i++) {
			length++;
			char chr = json.charAt(i);
			if (chr == '"') {
				if (last != '\\') {
					inContent = inContent ? false : true;
					continue;
				}
			}
			if (!inContent) {
				if (chr == '{' || chr == '[') {
					JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
					ObjectWrapper<?> childWrapper = interpreter(json,
							TypeUtil.getTypeEntityByType(
									jsonFieldEntity == null ? null : jsonFieldEntity.getField().getGenericType()),
							i);
					if (jsonFieldEntity != null) {
						setFieldValue(object, jsonFieldEntity, childWrapper.getObject());
						i += childWrapper.getLength();
						length += childWrapper.getLength();
					}
					field = null;
					continue;
				}
				// 出栈
				if (chr == output.getOutputSymbol()) {
					if (field != null) {
						JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
						if (jsonFieldEntity != null) {
							setFieldValue(object, jsonFieldEntity, temp.toString());
						}
					}
					isString = false;
					break;
				}
				if (chr == ',') {
					if (field != null) {
						JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
						if (jsonFieldEntity != null) {
							setFieldValue(object, jsonFieldEntity, ValueConvert.convert(temp, isString));
						}
					}
					temp = new StringBuilder();
					isString = false;
					continue;
				}
				if (chr == ':') {
					field = temp.toString();
					temp = new StringBuilder();
					isString = false;
					continue;
				}
			}
			last = chr;
			if (!isString && chr == ' ') {
				continue;
			}
			// 读取内容
			temp.append(chr);
		}
		wrapper.setObject((T) object);
		wrapper.setLength(length);
		return wrapper;
	}

}
