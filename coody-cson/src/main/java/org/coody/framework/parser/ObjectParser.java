package org.coody.framework.parser;

import org.coody.framework.entity.JsonFieldEntity;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.parser.iface.AbstractParser;
import org.coody.framework.util.FieldUtil;

@SuppressWarnings("unchecked")
public class ObjectParser extends AbstractParser {

	@Override
	public <T> ObjectWrapper<T> doParser(String json, TypeEntity type) {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		StringBuilder sbBuilder = new StringBuilder();

		boolean inContent = false;
		String field = null;
		Object object = type.newInstance();
		ObjectWrapper<T> wrapper = new ObjectWrapper<T>();
		for (int i = 1; i < json.length(); i++) {
			wrapper.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				inContent = inContent ? false : true;
				continue;
			}
			if (!inContent) {
				if (chr == '{' || chr == '[') {
					JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
					if (jsonFieldEntity != null) {
						ObjectWrapper<?> childWrapper = parser(json.substring(i, json.length()),
								new TypeEntity(jsonFieldEntity.getField().getType()));
						setFieldValue(object, jsonFieldEntity, childWrapper.getObject());
						i += childWrapper.getOffset();
					}
					field = null;
					continue;
				}
				// 出栈
				if (chr == output) {
					if (field != null) {
						JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
						if (jsonFieldEntity != null) {
							setFieldValue(object, jsonFieldEntity, sbBuilder.toString());
						}
					}
					break;
				}
				if (chr == ',') {
					if (field != null) {
						JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
						if (jsonFieldEntity != null) {
							setFieldValue(object, jsonFieldEntity, sbBuilder.toString());
						}
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
		wrapper.setObject((T) object);
		return wrapper;
	}

}
