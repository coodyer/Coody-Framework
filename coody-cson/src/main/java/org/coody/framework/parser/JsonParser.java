package org.coody.framework.parser;

import org.coody.framework.entity.CsonArray;
import org.coody.framework.entity.CsonObject;
import org.coody.framework.entity.JsonFieldEntity;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.exception.CsonException;
import org.coody.framework.instance.ReflectInstancer;
import org.coody.framework.instance.iface.CsonInstancer;
import org.coody.framework.util.FieldUtil;

public class JsonParser {

	private static CsonInstancer csonInstancer = new ReflectInstancer();

	public static CsonArray parseCsonArray(String json) {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		StringBuilder sbBuilder = new StringBuilder();

		boolean inContent = false;

		CsonArray cson = new CsonArray();
		for (int i = 1; i < json.length(); i++) {
			cson.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				inContent = inContent ? false : true;
				continue;
			}
			if (!inContent) {
				if (chr == '[') {
					CsonArray child = parseCsonArray(json.substring(i, json.length()));
					cson.add(child);
					i += child.getOffset();
					continue;
				}
				if (chr == '{') {
					CsonObject child = parseCsonObject(json.substring(i, json.length()));
					cson.add(child);
					i += child.getOffset();
					continue;
				}
				// 出栈
				if (chr == output) {
					// 完成解析
					cson.add(sbBuilder);
					break;
				}
				if (chr == ',') {
					cson.add(sbBuilder);
					sbBuilder = new StringBuilder();
					continue;
				}
				if (chr == ':') {
					sbBuilder = new StringBuilder();
					continue;
				}
			}
			// 读取内容
			sbBuilder.append(chr);
		}
		return cson;
	}

	public static CsonObject parseCsonObject(String json) {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		StringBuilder sbBuilder = new StringBuilder();

		boolean inContent = false;

		String field = null;
		CsonObject cson = new CsonObject();
		for (int i = 1; i < json.length(); i++) {
			cson.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				inContent = inContent ? false : true;
				continue;
			}
			if (!inContent) {
				if (chr == '[') {
					CsonArray child = parseCsonArray(json.substring(i, json.length()));
					cson.put(field, child);
					i += child.getOffset();
					field = null;
					continue;
				}
				if (chr == '{') {
					CsonObject child = parseCsonObject(json.substring(i, json.length()));
					cson.put(field, child);
					i += child.getOffset();
					field = null;
					continue;
				}
				// 出栈
				if (chr == output) {
					if (field != null) {
						cson.put(field, sbBuilder);
					}
					break;
				}
				if (chr == ',') {
					if (field != null) {
						cson.put(field, sbBuilder);
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
		return cson;
	}

	public static ObjectWrapper parseObject(String json, Class<?> clazz) {
		json = json.trim();
		char output = getOutputSymbol(json.charAt(0));
		StringBuilder sbBuilder = new StringBuilder();

		boolean inContent = false;
		String field = null;
		Object object = csonInstancer.createInstance(clazz);
		ObjectWrapper wrapper = new ObjectWrapper();
		for (int i = 1; i < json.length(); i++) {
			wrapper.setOffset(i);
			char chr = json.charAt(i);
			if (chr == '"') {
				inContent = inContent ? false : true;
				continue;
			}
			if (!inContent) {
				if (chr == '[') {
					JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
					if (jsonFieldEntity != null) {
						CsonArray child = parseCsonArray(json.substring(i, json.length()));
						// cson.put(field, child);
						setFieldValue(object, field, child);
						i += child.getOffset();
					}
					field = null;
					continue;
				}
				if (chr == '{') {
					JsonFieldEntity jsonFieldEntity = FieldUtil.getDeclaredField(object.getClass(), field);
					if (jsonFieldEntity != null) {
						ObjectWrapper childWrapper = parseObject(json.substring(i, json.length()),
								jsonFieldEntity.getField().getType());
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
		wrapper.setObject(object);
		return wrapper;
	}

	private static void setFieldValue(Object object, String field, Object value) {
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

	private static void setFieldValue(Object object, JsonFieldEntity field, Object value) {
		try {
			if (field == null) {
				return;
			}
			if (value == null) {
				return;
			}
			field.getField().set(object, FieldUtil.parseValue(value, field.getField().getType()));
		} catch (Exception e) {
			throw new CsonException("字段赋值失败>>" + field.getField().getName(), e);
		}
	}

	private static Character getOutputSymbol(char chr) {
		if (chr == '{') {
			return '}';
		}
		if (chr == '[') {
			return ']';
		}
		return null;
	}

}
