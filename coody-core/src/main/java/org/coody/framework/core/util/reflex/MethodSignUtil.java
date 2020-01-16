package org.coody.framework.core.util.reflex;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coody.framework.Cson;
import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.encrypt.EncryptUtil;

public class MethodSignUtil {

	public static String getKeyByFields(Class<?> clazz, Method method, Object[] parameters, String key,
			String[] fields) {
		if (CommonUtil.isNullOrEmpty(key)) {
			key = getKeyByMethod(clazz, method);
		}
		StringBuilder paraKey = new StringBuilder();
		for (String field : fields) {
			Object paraValue = MethodSignUtil.getMethodParameterValue(method, field, parameters);
			paraKey.append(Cson.toJson(paraValue));
			if (!CommonUtil.isNullOrEmpty(paraValue) && field != fields[fields.length - 1]) {
				paraKey.append("_");
			}
		}
		if (paraKey.toString().length() > 32) {
			return key + ":" + EncryptUtil.md5(paraKey.toString());
		}
		return key + ":" + paraKey.toString();
	}

	// 将对象内所有字段名、字段值拼接成字符串，用于缓存Key
	public static String getKeyByParameters(Object... parameters) {
		if (CommonUtil.isNullOrEmpty(parameters)) {
			return "";
		}
		StringBuilder content = new StringBuilder();
		for (Object line : parameters) {
			if (CommonUtil.isNullOrEmpty(line)) {
				content.append("null").append("-");
				continue;
			}
			if (BaseModel.class.isAssignableFrom(line.getClass())) {
				content.append(Cson.toJson(line)).append("-");
				continue;
			}
			content.append(line.toString()).append("-");
			continue;
		}
		if (content.length() <= 48) {
			return content.toString();
		}
		return EncryptUtil.md5(content.toString());
	}

	public static String getMethodUnionKey(Method method) {
		String paraKey = "";
		List<FieldEntity> entitys = PropertUtil.getMethodParameters(method);
		if (!CommonUtil.isNullOrEmpty(entitys)) {
			Set<String> methodParas = new HashSet<String>();
			for (FieldEntity entity : entitys) {
				String methodParaLine = entity.getFieldType().getName() + " " + entity.getFieldName();
				methodParas.add(methodParaLine);
			}
			paraKey = CommonUtil.splicing(methodParas, ",");
		}
		Class<?> clazz = PropertUtil.getClass(method);
		String methodKey = clazz.getName() + "." + method.getName() + "(" + paraKey + ")";
		return methodKey;
	}

	public static String getMethodKey(Method method) {
		Class<?> clazz = PropertUtil.getClass(method);
		return getKeyByMethod(clazz, method);
	}

	public static String getGeneralKeyByMethod(Method method) {
		StringBuilder sb = new StringBuilder(method.getReturnType().getName());
		sb.append("=").append(method.getName());
		Class<?>[] paraTypes = method.getParameterTypes();
		sb.append("(");
		if (!CommonUtil.isNullOrEmpty(paraTypes)) {
			for (int i = 0; i < paraTypes.length; i++) {
				sb.append(paraTypes[i].getName());
				if (i < paraTypes.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public static String getKeyByMethod(Class<?> clazz, Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName()).append(".").append(method.getName());
		Class<?>[] paraTypes = method.getParameterTypes();
		sb.append("(");
		if (!CommonUtil.isNullOrEmpty(paraTypes)) {
			for (int i = 0; i < paraTypes.length; i++) {
				sb.append(paraTypes[i].getSimpleName());
				if (i < paraTypes.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return sb.toString().replace(".", ":");
	}

	public static Object getMethodParameterValue(Method method, String fieldName, Object[] parameters) {
		List<FieldEntity> entitys = PropertUtil.getMethodParameters(method);
		if (CommonUtil.isNullOrEmpty(entitys)) {
			return "";
		}
		String[] fields = fieldName.split("\\.");
		FieldEntity entity = (FieldEntity) PropertUtil.getByList(entitys, "fieldName", fields[0]);
		if (CommonUtil.isNullOrEmpty(entity)) {
			return "";
		}
		Object para = parameters[entitys.indexOf(entity)];
		if (fields.length > 1 && para != null) {
			for (int i = 1; i < fields.length; i++) {
				para = PropertUtil.getFieldValue(para, fields[i]);
			}
		}
		return para;
	}
}
