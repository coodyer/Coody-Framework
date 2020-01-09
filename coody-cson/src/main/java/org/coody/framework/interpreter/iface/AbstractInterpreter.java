package org.coody.framework.interpreter.iface;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.convert.ValueConvert;
import org.coody.framework.entity.JsonFieldEntity;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.entity.TypeEntity;
import org.coody.framework.exception.BadFormatException;
import org.coody.framework.exception.CsonException;
import org.coody.framework.interpreter.ArrayInterpreter;
import org.coody.framework.interpreter.CollectionInterpreter;
import org.coody.framework.interpreter.MapInterpreter;
import org.coody.framework.interpreter.ObjectInterpreter;
import org.coody.framework.util.FieldUtil;
import org.coody.framework.util.TypeUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractInterpreter {

	protected static AbstractInterpreter arrayInterpreter = new ArrayInterpreter();

	protected static AbstractInterpreter collectionInterpreter = new CollectionInterpreter();

	protected static AbstractInterpreter mapInterpreter = new MapInterpreter();

	protected static AbstractInterpreter objectInterpreter = new ObjectInterpreter();

	public static <T> ObjectWrapper<T> interpreter(String json, Class<T> clazz) {
		return interpreter(json, TypeUtil.getTypeEntityByType((Type) clazz));
	}

	public static <T> ObjectWrapper<T> interpreter(String json, TypeAdapter<T> adapter) {
		return interpreter(json, TypeUtil.getTypeEntityByType(adapter.getType()));
	}

	public static <T> ObjectWrapper interpreter(String json, TypeEntity type) {
		return interpreter(json, type, 0);
	}

	public static <T> ObjectWrapper interpreter(String json, TypeEntity type, int offset) {
		json = json.trim();

		offset = offset - 1;
		OutputSymbolWrapper output = getOutputSymbol(json, offset);
		if (output == null) {
			throw new BadFormatException("错误的Json格式");
		}
		offset += output.getLength() - 1;
		Character outputCharacter = output.getOutputSymbol();
		if (outputCharacter == ']') {
			if (type == null) {
				type = TypeUtil.getTypeEntityByType(new TypeAdapter<List<Object>>() {
				}.getType());
			}
			if (type.getCurrent().isArray()) {
				return arrayInterpreter.doInterpreter(json, type, offset);
			}
			return collectionInterpreter.doInterpreter(json, type, offset);
		}
		if (outputCharacter == '}') {
			if (type == null) {
				type = TypeUtil.getTypeEntityByType(new TypeAdapter<Map<Object, Object>>() {
				}.getType());
			}
			if (Map.class.isAssignableFrom(type.getCurrent())) {
				if (type.getActuals() == null || type.getActuals().size() < 2) {
					type = TypeUtil.getTypeEntityByType(new TypeAdapter<Map<Object, Object>>() {
					}.getType());
				}
				return mapInterpreter.doInterpreter(json, type, offset);
			}
			if (type.getCurrent() == Object.class) {
				type = TypeUtil.getTypeEntityByType(new TypeAdapter<Map<Object, Object>>() {
				}.getType());
				return mapInterpreter.doInterpreter(json, type, offset);
			}
			return objectInterpreter.doInterpreter(json, type, offset);
		}
		return null;
	}

	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param type
	 * @param outputCharacter
	 * @param offset
	 * @return
	 */
	public abstract <T> ObjectWrapper<T> doInterpreter(String json, TypeEntity type, int offset);

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
			field.getField().set(object, ValueConvert.convert(value, field.getField().getType()));
		} catch (Exception e) {
			throw new CsonException("字段赋值失败>>" + field.getField().getName(), e);
		}
	}

	protected static OutputSymbolWrapper getOutputSymbol(String json, int offset) {
		int length = 0;
		for (int i = offset; i < json.length(); i++) {
			length++;
			if (i < 0) {
				continue;
			}
			char chr = json.charAt(i);
			if (chr == '{') {
				return new OutputSymbolWrapper('}', length);
			}
			if (chr == '[') {
				return new OutputSymbolWrapper(']', length);
			}
		}
		return null;
	}

	protected static class OutputSymbolWrapper {

		private Character outputSymbol;

		private int length;

		public OutputSymbolWrapper(Character outputSymbol, int length) {
			super();
			this.outputSymbol = outputSymbol;
			this.length = length;
		}

		public Character getOutputSymbol() {
			return outputSymbol;
		}

		public void setOutputSymbol(Character outputSymbol) {
			this.outputSymbol = outputSymbol;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}
	}
}
