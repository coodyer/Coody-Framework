package org.coody.framework.parser.iface;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.entity.JsonFieldEntity;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.exception.BadFormatException;
import org.coody.framework.exception.CsonException;
import org.coody.framework.parser.ArrayParser;
import org.coody.framework.parser.CollectionParser;
import org.coody.framework.parser.MapParser;
import org.coody.framework.parser.ObjectParser;
import org.coody.framework.util.FieldUtil;
import org.coody.framework.util.TypeUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractParser {

	protected static AbstractParser arrayParser = new ArrayParser();

	protected static AbstractParser collectionParser = new CollectionParser();

	protected static AbstractParser mapParser = new MapParser();

	protected static AbstractParser objectParser = new ObjectParser();

	public static <T> ObjectWrapper<T> parser(String json, Class<?> clazz) {
		return parser(json, TypeUtil.getTypeEntityByType((Type) clazz));
	}

	public static <T> ObjectWrapper<T> parser(String json, TypeAdapter<T> adapter) {
		return parser(json, TypeUtil.getTypeEntityByType(adapter.getType()));
	}

	public static <T> ObjectWrapper parser(String json, TypeEntity type) {
		json = json.trim();
		Character output = getOutputSymbol(json.charAt(0));
		if (output == null) {
			throw new BadFormatException("错误的Json格式");
		}
		if (output == ']') {
			if (type.getCurrent().isArray()) {
				return arrayParser.doParser(json, type);
			}
			type = TypeUtil.getTypeEntityByType(new TypeAdapter<List<Object>>() {
			}.getType());
			return collectionParser.doParser(json, type);
		}
		if (output == '}') {
			if (Map.class.isAssignableFrom(type.getCurrent())) {
				return mapParser.doParser(json, type);
			}
			if (type.getCurrent() == Object.class) {
				type = TypeUtil.getTypeEntityByType(new TypeAdapter<Map<Object, Object>>() {
				}.getType());
				return mapParser.doParser(json, type);
			}
			return objectParser.doParser(json, type);
		}
		return null;
	}

	public abstract <T> ObjectWrapper<T> doParser(String json, TypeEntity type);

	protected static void setFieldValue(Object object, String field, Object value) {
		try {
			if (value == null) {
				return;
			}
			JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
			setFieldValue(object, jsonFieldEntity, value);
		} catch (Exception e) {
			throw new CsonException("字段赋值失败>>" + field);
		}
	}

	protected static void setFieldValue(Object object, JsonFieldEntity field, Object value) {
		try {
			if (field == null) {
				return;
			}
			if (value == null) {
				return;
			}
			if (field.getIsIgonre()) {
				return;
			}
			field.getField().set(object, FieldUtil.parseValue(value, field.getField().getType()));
		} catch (Exception e) {
			throw new CsonException("字段赋值失败>>" + field.getField().getName(), e);
		}
	}

	protected static Character getOutputSymbol(char chr) {
		if (chr == '{') {
			return '}';
		}
		if (chr == '[') {
			return ']';
		}
		return null;
	}
}
